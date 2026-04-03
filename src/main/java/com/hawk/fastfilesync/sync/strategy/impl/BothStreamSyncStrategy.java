package com.hawk.fastfilesync.sync.strategy.impl;

import com.hawk.fastfilesync.enums.FileStatus;
import com.hawk.fastfilesync.model.BufferSnapshot;
import com.hawk.fastfilesync.sync.conflict.ConflictHandler;
import com.hawk.fastfilesync.sync.executor.SyncExecutor;
import com.hawk.fastfilesync.sync.strategy.StreamSyncStrategy;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Stream synchronization strategy that propagates changes between both sources.
 */
public class BothStreamSyncStrategy implements StreamSyncStrategy {
  private final SyncExecutor syncExecutor;
  private final ConflictHandler conflictHandler;

  /**
   * Creates a BothStreamSyncStrategy with the specified executor and conflict handler.
   *
   * @param syncExecutor the executor used to perform file synchronization
   * @param conflictHandler the handler used to resolve conflicts
   */
  public BothStreamSyncStrategy(SyncExecutor syncExecutor,
                                ConflictHandler conflictHandler) {
    this.syncExecutor = syncExecutor;
    this.conflictHandler = conflictHandler;
  }

  /**
   * Handles synchronization for the entry at the given index.
   * Copies missing entries between sources or delegates conflict resolution.
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
      Path target = leftRoot.resolve(snapshot.getRelativePath(index));

      syncExecutor.execute(source, target, snapshot.getFlag(index));
    } else if (status == FileStatus.LEFT_ONLY.getValue()) {
      Path source = leftRoot.resolve(snapshot.getRelativePath(index));
      Path target = rightRoot.resolve(snapshot.getRelativePath(index));

      syncExecutor.execute(source, target, snapshot.getFlag(index));
    } else if (status == FileStatus.CONFLICT.getValue()) {
      conflictHandler.handle(snapshot, index, leftRoot, rightRoot, targetRoot);
    }
  }

}
