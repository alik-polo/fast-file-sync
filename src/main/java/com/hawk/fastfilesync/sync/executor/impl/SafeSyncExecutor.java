package com.hawk.fastfilesync.sync.executor.impl;

import com.hawk.fastfilesync.enums.FileType;
import com.hawk.fastfilesync.sync.executor.SyncExecutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * SyncExecutor implementation that safely synchronizes files and handles symbolic links.
 */
public class SafeSyncExecutor implements SyncExecutor {

  /**
   * Executes synchronization of the given source to the target based on the file type.
   * Skips unknown, directory, or other types, and handles regular files and symbolic links.
   *
   * @param source the source path to synchronize
   * @param target the target path for synchronization
   * @param type the type of the file as defined in FileType
   * @throws IOException if an I/O error occurs during synchronization
   */
  @Override
  public void execute(Path source, Path target, byte type) throws IOException {
    if (type == FileType.OTHER.getValue()) {
      return;
    }

    if (type == FileType.UNKNOWN.getValue()) {
      return;
    }

    if (type == FileType.DIRECTORY.getValue()) {
      return;
    }

    if (type == FileType.SYMLINK.getValue()) {
      handleSymlink(source, target);
      return;
    }

    if (type == FileType.REGULAR_FILE.getValue()) {
      handleRegularFiles(source, target);
    }
  }

  /**
   * Handles synchronization for regular files using a temporary file and atomic move.
   *
   * @param source the source file to copy
   * @param target the target file path
   * @throws IOException if an I/O error occurs during copying or moving
   */
  private void handleRegularFiles(Path source, Path target) throws IOException {
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

  /**
   * Handles synchronization for symbolic links by recreating the link at the target.
   *
   * @param source the source symbolic link
   * @param target the target path for the symbolic link
   * @throws IOException if an I/O error occurs while reading or creating the link
   */
  private void handleSymlink(Path source, Path target) throws IOException {
    Path link = Files.readSymbolicLink(source);
    Files.createSymbolicLink(target, link);
  }

}
