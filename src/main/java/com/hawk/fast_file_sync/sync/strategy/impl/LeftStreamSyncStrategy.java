package com.hawk.fast_file_sync.sync.strategy.impl;

import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Stream synchronization strategy that prioritizes the left source.
 */
public class LeftStreamSyncStrategy implements StreamSyncStrategy {
  private final SyncExecutor syncExecutor;
  private final ConflictHandler conflictHandler;

  /**
   * Creates a LeftStreamSyncStrategy with the specified executor and conflict handler.
   *
   * @param syncExecutor the executor used to perform file synchronization
   * @param conflictHandler the handler used to resolve conflicts
   */
  public LeftStreamSyncStrategy(SyncExecutor syncExecutor,
                                ConflictHandler conflictHandler) {
    this.syncExecutor = syncExecutor;
    this.conflictHandler = conflictHandler;
  }

  /**
   * Handles synchronization for the entry at the given index.
   * Executes direct copy if the entry exists only on the right,
   * or delegates to the conflict handler if a conflict is detected.
   *
   * @param snapshot the buffer snapshot containing entry metadata
   * @param index the index of the entry to process
   * @param leftRoot the left source root directory
   * @param rightRoot the right source root directory
   * @param targetRoot the target root directory
   * @throws IOException if an I/O error occurs during synchronization
   */
  @Override
  public void handle(BufferSnapshot snapshot,
                     int index,
                     Path leftRoot,
                     Path rightRoot,
                     Path targetRoot) throws IOException {

    byte status = snapshot.getStatus(index);

    if (status == FileStatus.RIGHT_ONLY.getValue()) {
      Path source = rightRoot.resolve(snapshot.getRelativePath(index));
      Path target = targetRoot.resolve(snapshot.getRelativePath(index));

      syncExecutor.execute(source, target, snapshot.getFlag(index));
    } else if (status == FileStatus.CONFLICT.getValue()) {
      conflictHandler.handle(snapshot, index, leftRoot, rightRoot, targetRoot);
    }
  }
}