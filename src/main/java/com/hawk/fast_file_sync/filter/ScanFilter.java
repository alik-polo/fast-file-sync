package com.hawk.fast_file_sync.filter;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Contract for filters that determine whether a file or directory should be processed.
 */
public interface ScanFilter {

  /**
   * Determines whether the given file or directory should be accepted for processing.
   *
   * @param path the path to the file or directory being tested
   * @param attrs the file attributes of the path
   * @return true if the file or directory should be processed, false otherwise
   */
  boolean accept(Path path, BasicFileAttributes attrs);

}
