package com.hawk.fastfilesync.filter.impl;

import com.hawk.fastfilesync.filter.ScanFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Filter that excludes files or directories that are missing or not readable.
 */
public class BrokenFilter implements ScanFilter {

  /**
   * Determines whether the given file or directory should be accepted.
   * Only existing and readable files or directories are accepted.
   *
   * @param path the path to the file or directory being tested
   * @param attrs the file attributes of the path
   * @return true if the file or directory exists and is readable, false otherwise
   */
  @Override
  public boolean accept(Path path, BasicFileAttributes attrs) {
    try {
      return Files.exists(path) && Files.isReadable(path);
    } catch (SecurityException e) {
      return false;
    }
  }

}
