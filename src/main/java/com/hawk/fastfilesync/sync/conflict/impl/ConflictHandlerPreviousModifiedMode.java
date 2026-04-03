package com.hawk.fastfilesync.sync.conflict.impl;

import com.hawk.fastfilesync.model.BufferSnapshot;
import com.hawk.fastfilesync.sync.conflict.ConflictHandler;
import com.hawk.fastfilesync.sync.executor.SyncExecutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConflictHandler implementation that resolves conflicts
 * by selecting the previously modified file.
 */
public class ConflictHandlerPreviousModifiedMode implements ConflictHandler {
  private final SyncExecutor syncExecutor;

  /**
   * ConflictHandler implementation that resolves conflicts
   * by selecting the previously modified file.
   */
  public ConflictHandlerPreviousModifiedMode(SyncExecutor syncExecutor) {
    this.syncExecutor = syncExecutor;
  }

  /**
   * Resolves a conflict by comparing last modified timestamps
   * and selecting the earlier modified file.
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
    Path leftPath = leftRoot.resolve(relative);
    Path rightPath = rightRoot.resolve(relative);

    long leftSize = Files.getLastModifiedTime(leftPath).toMillis();
    long rightSize = Files.getLastModifiedTime(rightPath).toMillis();

    Path source = (leftSize < rightSize) ? leftPath : rightPath;

    Path target;
    if (targetRoot != null) {
      target = targetRoot.resolve(relative);
    } else {
      target = (source == leftPath) ? rightPath : leftPath;
    }

    syncExecutor.execute(source, target, snapshot.getFlag(index));
  }
}