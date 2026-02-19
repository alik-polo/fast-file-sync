package com.hawk.fast_file_sync.diff;

import com.hawk.fast_file_sync.model.BufferSnapshot;

/**
 * Contract for processing differences between two sets of file metadata.
 */
public interface StreamDiffStrategy {

  /**
   * Processes an entry from the left source.
   *
   * @param relativePathHash the hash of the relative path
   * @param relativePath the relative path string
   * @param size the file size in bytes
   * @param modified the last modification timestamp in milliseconds
   * @param flag the file type flag
   */
  void addLeft(long relativePathHash,
               String relativePath,
               long size,
               long modified,
               byte flag);

  /**
   * Processes an entry from the right source.
   *
   * @param relativePathHash the hash of the relative path
   * @param relativePath the relative path string
   * @param size the file size in bytes
   * @param modified the last modification timestamp in milliseconds
   * @param flag the file type flag
   */
  void addRight(long relativePathHash,
                String relativePath,
                long size,
                long modified,
                byte flag);

  /**
   * Returns an immutable snapshot of the current diff state.
   *
   * @return a BufferSnapshot containing the accumulated diff entries
   */
  BufferSnapshot snapshot();

}
