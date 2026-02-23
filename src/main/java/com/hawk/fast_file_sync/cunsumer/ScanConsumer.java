package com.hawk.fast_file_sync.cunsumer;

/**
 * Contract for handling metadata of files and directories discovered during a scan.
 */
public interface ScanConsumer {

  /**
   * Processes the metadata of a file or directory.
   *
   * @param relativePathHash the hash of the relative path for quick comparison
   * @param relativePath the path of the file or directory relative to the scan root
   * @param size the size of the file or directory in bytes
   * @param modified the last modification timestamp in milliseconds since epoch
   * @param flag a byte representing file-specific flags
   */
  void accept(long relativePathHash,
              String relativePath,
              long size,
              long modified,
              byte flag);

}