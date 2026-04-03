package com.hawk.fastfilesync.filter.impl;

import com.hawk.fastfilesync.filter.ScanFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Filter that excludes hidden files and directories from being processed.
 */
public class HiddenFilter implements ScanFilter {

  /**
   * Determines whether the given file or directory should be accepted.
   * Hidden files or directories are rejected. If an I/O error occurs while
   * checking the hidden attribute, the file is rejected.
   *
   * @param path the path to the file or directory being tested
   * @param attrs the file attributes of the path
   * @return true if the file or directory is not hidden, false otherwise
   */
  @Override
  public boolean accept(Path path, BasicFileAttributes attrs) {
    try {
      return !Files.isHidden(path);
    } catch (IOException e) {
      return false;
    }
  }

}