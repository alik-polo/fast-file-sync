package com.hawk.fastfilesync.sync.executor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Executes file synchronization from a source path to a target path.
 */
public interface SyncExecutor {

  /**
   * Copies or synchronizes a file or directory from the source to the target.
   *
   * @param source the source path to copy from
   * @param target the target path to copy to
   * @throws IOException if an I/O error occurs during execution
   */
  void execute(Path source, Path target, byte type) throws IOException;

}