package com.hawk.fast_file_sync.app.session;

import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.buffer.EntryBuffer;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.diff.StreamDiffStrategy;
import com.hawk.fast_file_sync.diff.factory.StreamDiffFactory;
import com.hawk.fast_file_sync.enums.ConflictOption;
import com.hawk.fast_file_sync.enums.DiffOption;
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
import com.hawk.fast_file_sync.sync.policy.impl.FastFailPolicy;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.factory.StreamSyncFactory;

import java.nio.file.Path;
import java.util.UUID;

public class AppSession implements AutoCloseable {
  private final String id;
  private final AppConfig config;
  private final CancellationToken cancellationToken;
  private final ReportConsumer reportConsumer;
  private final ErrorHandlingPolicy policy;

  private BufferSnapshot snapshot;

  public AppSession(AppConfig config,
                    ReportConsumer reportConsumer,
                    ErrorHandlingPolicy policy) {
    id = UUID.randomUUID().toString();
    cancellationToken = new SimpleCancellationToken();
    this.config = config;
    this.reportConsumer = reportConsumer;
    this.policy = policy;
  }

  public void runScan(Path left,
                      Path right,
                      DiffOption diffOption)
      throws OperationCancelledException, TraversalException {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();

    StreamDiffStrategy diffStrategy = StreamDiffFactory.getStrategy(
        diffOption,
        buffer,
        index
    );

    FileScanner scanner = config.fileScanner(null);

    scanner.scan(left, diffStrategy::addLeft, cancellationToken);
    scanner.scan(right, diffStrategy::addRight, cancellationToken);

    snapshot = diffStrategy.snapshot();
  }

  public void runSync(Path left,
                      Path right,
                      Path target,
                      SyncOption syncOption,
                      ConflictOption conflictOption)
      throws OperationCancelledException, TraversalException {

    if (snapshot == null) {
      throw new IllegalArgumentException();
    }

    ConflictHandler conflictHandler = ConflictHandlerFactory.getConflictMode(
        conflictOption,
        config.syncExecutor()
    );

    StreamSyncStrategy syncStrategy = StreamSyncFactory.getStrategy(
        syncOption,
        config.syncExecutor(),
        conflictHandler
    );

    config.syncEngine(reportConsumer, policy).process(
        left,
        right,
        target,
        snapshot,
        syncStrategy,
        cancellationToken
    );
  }

  private void cancel() {
    cancellationToken.cancel();
  }

  @Override
  public void close() throws Exception {
    cancellationToken.cancel();
    snapshot = null;
  }

  public String getId() {
    return id;
  }
}

