package com.hawk.fast_file_sync.app.session;

import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.buffer.EntryBuffer;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.diff.StreamDiffStrategy;
import com.hawk.fast_file_sync.diff.factory.StreamDiffFactory;
import com.hawk.fast_file_sync.enums.ConflictOption;
import com.hawk.fast_file_sync.enums.DiffOption;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.enums.SyncOption;
import com.hawk.fast_file_sync.exception.OperationCancelledException;
import com.hawk.fast_file_sync.exception.TraversalException;
import com.hawk.fast_file_sync.index.Index;
import com.hawk.fast_file_sync.index.impl.InMemoryIndex;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.model.CancellationToken;
import com.hawk.fast_file_sync.model.SimpleCancellationToken;
import com.hawk.fast_file_sync.scan.FileScanner;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.conflict.factory.ConflictHandlerFactory;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.factory.StreamSyncFactory;
import java.nio.file.Path;
import java.util.UUID;

public class AppSession implements AutoCloseable {

  private enum State {
    CREATED,
    SCANNED,
    SYNCED,
    CLOSED
  }

  private final String id;
  private final AppConfig config;
  private final CancellationToken cancellationToken;
  private final ReportConsumer reportConsumer;

  private Path left;
  private Path right;

  private BufferSnapshot snapshot;
  private State state = State.CREATED;

  public AppSession(AppConfig config,
                    ReportConsumer reportConsumer) {
    this.id = UUID.randomUUID().toString();
    this.config = config;
    this.reportConsumer = reportConsumer;
    this.cancellationToken = new SimpleCancellationToken();
  }

  public void runScan(Path left,
                      Path right,
                      DiffOption diffOption,
                      ReportConsumer localReportConsumer)
      throws OperationCancelledException, TraversalException {

    ensureNotClosed();

    this.left = left;
    this.right = right;

    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();

    StreamDiffStrategy diffStrategy = StreamDiffFactory.getStrategy(
        diffOption,
        buffer,
        index
    );

    FileScanner scanner = config.fileScanner();

    localReportConsumer.info("Started scanning...");

    scanner.scan(left, diffStrategy::addLeft, cancellationToken);
    scanner.scan(right, diffStrategy::addRight, cancellationToken);

    this.snapshot = diffStrategy.snapshot();
    this.state = State.SCANNED;

    int same = 0;
    int leftOnly = 0;
    int rightOnly = 0;

    for (int i = 0; i < snapshot.getSnapshotSize(); i++) {
      if (snapshot.getStatus(i) == FileStatus.SAME.getValue()) {
        same++;
      } else if (snapshot.getStatus(i) == FileStatus.LEFT_ONLY.getValue()) {
        leftOnly++;
      } else if (snapshot.getStatus(i) == FileStatus.RIGHT_ONLY.getValue()) {
        rightOnly++;
      }

      reportConsumer.info(FileStatus.fromValue(
          snapshot.getStatus(i)) + " | " + snapshot.getRelativePath(i)
      );
    }

    reportConsumer.info("Completed scanning. Total files: " + snapshot.getSnapshotSize());
    reportConsumer.info("Same files: " + same);
    reportConsumer.info("Left only files: " + leftOnly);
    reportConsumer.info("Right only files: " + rightOnly);
  }

  public void runSync(Path left,
                      Path right,
                      Path target,
                      SyncOption syncOption,
                      ConflictOption conflictOption)
      throws OperationCancelledException, TraversalException {

    ensureState(State.SCANNED);

    ConflictHandler conflictHandler = ConflictHandlerFactory.getConflictMode(
        conflictOption,
        config.executor()
    );

    StreamSyncStrategy syncStrategy = StreamSyncFactory.getStrategy(
        syncOption,
        config.executor(),
        conflictHandler
    );

    ErrorHandlingPolicy policy = config.errorPolicy();
    config.syncEngine(reportConsumer, policy).process(
        left,
        right,
        target,
        snapshot,
        syncStrategy,
        cancellationToken
    );

    this.state = State.SYNCED;
  }

  public Path getLeft() {
    ensureState(State.SCANNED);
    return left;
  }

  public Path getRight() {
    ensureState(State.SCANNED);
    return right;
  }

  @Override
  public void close() {
    if (state == State.CLOSED) {
      return;
    }

    cancellationToken.cancel();
    snapshot = null;

    left = null;
    right = null;

    state = State.CLOSED;
  }

  public String getId() {
    return id;
  }

  private void ensureState(State expected) {
    if (state != expected) {
      throw new IllegalStateException(
          "Invalid session state. Expected: " + expected + ", actual: " + state
      );
    }
  }

  private void ensureNotClosed() {
    if (state == State.CLOSED) {
      throw new IllegalStateException("Session is closed");
    }
  }
}