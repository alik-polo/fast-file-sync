package com.hawk.fast_file_sync.sync.conflict.impl;

import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConflictHandler implementation that resolves conflicts
 * by selecting the latest modified file.
 */
public class ConflictHandlerLatestModifiedMode implements ConflictHandler {
  private final SyncExecutor syncExecutor;

  /**
   * ConflictHandler implementation that resolves conflicts
   * by selecting the latest modified file.
   */
  public ConflictHandlerLatestModifiedMode(SyncExecutor syncExecutor) {
    this.syncExecutor = syncExecutor;
  }

  /**
   * Resolves a conflict by comparing last modified timestamps
   * and selecting the most recently modified file.
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

    long leftModified = Files.getLastModifiedTime(leftPath).toMillis();
    long rightModified = Files.getLastModifiedTime(rightPath).toMillis();

    Path source = (leftModified >= rightModified) ? leftPath : rightPath;

    Path target;
    if (targetRoot != null) {
      target = targetRoot.resolve(relative);
    } else {
      target = (source == leftPath) ? rightPath : leftPath;
    }

    syncExecutor.execute(source, target, snapshot.getFlag(index));
  }
}