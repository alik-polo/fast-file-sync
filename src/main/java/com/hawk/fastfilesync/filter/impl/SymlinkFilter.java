package com.hawk.fastfilesync.filter.impl;

import com.hawk.fastfilesync.filter.ScanFilter;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Filter that excludes symbolic links from being processed.
 */
public class SymlinkFilter implements ScanFilter {

  /**
   * Determines whether the given file or directory should be accepted.
   * Symbolic links are rejected, all other types are accepted.
   *
   * @param path the path to the file or directory being tested
   * @param attrs the file attributes of the path
   * @return true if the file or directory is not a symbolic link, false otherwise
   */
  @Override
  public boolean accept(Path path, BasicFileAttributes attrs) {
    // log
    return !attrs.isSymbolicLink();
  }

}
