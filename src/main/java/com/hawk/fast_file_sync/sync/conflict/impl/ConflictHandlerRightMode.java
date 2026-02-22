package com.hawk.fast_file_sync.sync.conflict.impl;

import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import java.io.IOException;
import java.nio.file.Path;

/**
 * ConflictHandler implementation that resolves conflicts
 * by selecting the right source.
 */
public class ConflictHandlerRightMode implements ConflictHandler {
  private final SyncExecutor syncExecutor;

  /**
   * ConflictHandler implementation that resolves conflicts
   * by selecting the right source.
   */
  public ConflictHandlerRightMode(SyncExecutor syncExecutor) {
    this.syncExecutor = syncExecutor;
  }

  /**
   * Resolves a conflict by selecting the file from the right source.
   * Synchronizes it either to the target root or to the left side.
   *
   * @param snapshot the buffer snapshot containing entry metadata
   * @param index the index of the conflicting entry
   * @param rightRoot the right source root directory
   * @param leftRoot the left source root directory
   * @param targetRoot the target root directory, or null for bidirectional sync
   * @throws IOException if an I/O error occurs during conflict resolution
   */
  @Override
  public void handle(BufferSnapshot snapshot,
                     int index,
                     Path leftRoot,
                     Path rightRoot,
                     Path targetRoot) throws IOException {

    String relative = snapshot.getRelativePath(index);
    Path source = rightRoot.resolve(relative);

    Path target;
    if (targetRoot != null) {
      target = targetRoot.resolve(relative);
    } else {
      target = leftRoot.resolve(relative);
    }

    syncExecutor.execute(source, target, snapshot.getFlag(index));
  }
}