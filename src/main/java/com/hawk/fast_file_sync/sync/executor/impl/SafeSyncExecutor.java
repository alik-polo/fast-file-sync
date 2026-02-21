package com.hawk.fast_file_sync.sync.executor.impl;

import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * SyncExecutor implementation that performs safe file synchronization
 * using temporary files and atomic moves.
 */
public class SafeSyncExecutor implements SyncExecutor {

  /**
   * Copies the source file to the target using a temporary file
   * and an atomic move to ensure safe replacement.
   *
   * @param source the source path to copy from
   * @param target the target path to copy to
   * @throws IOException if an I/O error occurs during synchronization
   */
  @Override
  public void execute(Path source, Path target) throws IOException {
    Files.createDirectories(target.getParent());

    Path tempTarget = target.resolveSibling(target.getFileName() + ".tmp");

    Files.copy(source, tempTarget);

    Files.move(
        tempTarget,
        target,
        StandardCopyOption.ATOMIC_MOVE,
        StandardCopyOption.REPLACE_EXISTING
    );
  }

}
