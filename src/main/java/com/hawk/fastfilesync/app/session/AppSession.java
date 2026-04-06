package com.hawk.fastfilesync.app.session;

import com.hawk.fastfilesync.app.config.AppConfig;
import com.hawk.fastfilesync.buffer.EntryBuffer;
import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.diff.StreamDiffStrategy;
import com.hawk.fastfilesync.diff.factory.StreamDiffFactory;
import com.hawk.fastfilesync.enums.ConflictOption;
import com.hawk.fastfilesync.enums.DiffOption;
import com.hawk.fastfilesync.enums.FileStatus;
import com.hawk.fastfilesync.enums.SyncOption;
import com.hawk.fastfilesync.exception.OperationCancelledException;
import com.hawk.fastfilesync.exception.TraversalException;
import com.hawk.fastfilesync.exception.UserException;
import com.hawk.fastfilesync.index.Index;
import com.hawk.fastfilesync.index.impl.InMemoryIndex;
import com.hawk.fastfilesync.model.BufferSnapshot;
import com.hawk.fastfilesync.model.CancellationToken;
import com.hawk.fastfilesync.model.SimpleCancellationToken;
import com.hawk.fastfilesync.scan.FileScanner;
import com.hawk.fastfilesync.sync.conflict.ConflictHandler;
import com.hawk.fastfilesync.sync.conflict.factory.ConflictHandlerFactory;
import com.hawk.fastfilesync.sync.policy.ErrorHandlingPolicy;
import com.hawk.fastfilesync.sync.strategy.StreamSyncStrategy;
import com.hawk.fastfilesync.sync.strategy.factory.StreamSyncFactory;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Represents an application session for scanning and synchronizing files between directories.
 */
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

  /**
   * Creates a new {@code AppSession} with the specified configuration and report consumer.
   *
   * @param config the application configuration.
   * @param reportConsumer the consumer to receive session reports.
   */
  public AppSession(AppConfig config,
                    ReportConsumer reportConsumer) {
    this.id = UUID.randomUUID().toString();
    this.config = config;
    this.reportConsumer = reportConsumer;
    this.cancellationToken = new SimpleCancellationToken();
  }

  /**
   * Runs a file scan on the given directories using the specified diff option.
   *
   * @param left the left directory path
   * @param right the right directory path
   * @param diffOption the difference calculation option
   * @param reportConsumer a consumer to report local scan progress
   * @throws OperationCancelledException if the scan is cancelled
   * @throws TraversalException if an error occurs during file traversal
   */
  public void runScan(Path left,
                      Path right,
                      DiffOption diffOption,
                      ReportConsumer reportConsumer) {

    ensureNotClosed();

    try {
      validatePaths(left, right);

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

      reportConsumer.operationNotice("Started scanning...");

      scanner.scan(left, diffStrategy::addLeft, cancellationToken);
      scanner.scan(right, diffStrategy::addRight, cancellationToken);

      this.snapshot = diffStrategy.snapshot();
      this.state = State.SCANNED;

      processStats();

    } catch (OperationCancelledException e) {
      throw e;
    } catch (TraversalException e) {
      throw new UserException("Failed to read files", e);
    } catch (Exception e) {
      throw new UserException("Scan failed", e);
    }
  }

  private void validatePaths(Path left, Path right) {

    if (left == null || right == null) {
      throw new UserException("Paths must not be null");
    }

    if (!left.toFile().exists()) {
      throw new UserException("Left directory does not exist");
    }

    if (!right.toFile().exists()) {
      throw new UserException("Right directory does not exist");
    }

    if (!left.toFile().isDirectory()) {
      throw new UserException("Left path is not a directory");
    }

    if (!right.toFile().isDirectory()) {
      throw new UserException("Right path is not a directory");
    }
  }

  private void processStats() {
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

    reportConsumer.operationNotice(
        "Completed scanning. Total files: " + snapshot.getSnapshotSize()
    );
    reportConsumer.info("Same files: " + same);
    reportConsumer.info("Left only files: " + leftOnly);
    reportConsumer.info("Right only files: " + rightOnly);
  }

  /**
   * Runs synchronization between two directories into the target directory.
   * Requires the session to have been scanned first.
   *
   * @param left the left directory path
   * @param right the right directory path
   * @param target the target directory path for synchronization
   * @param syncOption the synchronization strategy
   * @param conflictOption the conflict resolution strategy
   * @throws OperationCancelledException if the sync is cancelled
   * @throws TraversalException if an error occurs during file traversal
   */
  public void runSync(Path left,
                      Path right,
                      Path target,
                      SyncOption syncOption,
                      ConflictOption conflictOption) {

    ensureState();

    try {

      validatePaths(left, right);

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

    } catch (OperationCancelledException e) {
      throw e;
    } catch (TraversalException e) {
      throw new UserException("Failed during sync", e);
    } catch (Exception e) {
      throw new UserException("Sync failed", e);
    }
  }

  /**
   * Returns the left directory path used in the scan.
   *
   * @return the left path
   * @throws IllegalStateException if the session has not been scanned yet
   */
  public Path getLeft() {
    ensureState();
    return left;
  }

  /**
   * Returns the right directory path used in the scan.
   *
   * @return the right path
   * @throws IllegalStateException if the session has not been scanned yet
   */
  public Path getRight() {
    ensureState();
    return right;
  }

  /**
   * Closes the session, cancelling any ongoing operations and releasing resources.
   */
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

  /**
   * Returns the unique identifier of this session.
   *
   * @return the session ID
   */
  public String getId() {
    return id;
  }

  private void ensureState() {
    if (state == State.CREATED || state == State.CLOSED) {
      throw new UserException("Session is not ready");
    }
  }

  private void ensureNotClosed() {
    if (state == State.CLOSED) {
      throw new UserException("Session is already closed");
    }
  }
}