package com.hawk.fast_file_sync.diff.impl;

import com.hawk.fast_file_sync.buffer.EntryBuffer;
import com.hawk.fast_file_sync.diff.StreamDiffStrategy;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.index.Index;
import com.hawk.fast_file_sync.model.BufferSnapshot;

/**
 * Fast implementation of StreamDiffStrategy that performs a shallow comparison between
 * two sets of file metadata.
 */
public class FastStreamDiffStrategy implements StreamDiffStrategy {
  private final EntryBuffer buffer;
  private final Index index;

  /**
   * Constructs a FastStreamDiffStrategy with the specified buffer and index.
   *
   * @param buffer the EntryBuffer used to store file entries
   * @param index the Index used to map path hashes to buffer positions
   */
  public FastStreamDiffStrategy(EntryBuffer buffer, Index index) {
    this.buffer = buffer;
    this.index = index;
  }

  /**
   * Adds a file entry from the left source to the diff.
   *
   * @param relativePathHash the hash of the file's relative path
   * @param relativePath the relative path of the file
   * @param size the size of the file in bytes
   * @param modified the last modification timestamp in milliseconds
   * @param flag the file type flag representing attributes such as file or directory
   */
  @Override
  public void addLeft(long relativePathHash,
                      String relativePath,
                      long size,
                      long modified,
                      byte flag) {

    buffer.add(
        relativePathHash,
        relativePath,
        size,
        modified,
        flag,
        FileStatus.LEFT_ONLY.getValue()
    );

    int idx = buffer.size() - 1;
    index.add(relativePathHash, idx);
  }

  /**
   * Adds a file entry from the right source to the diff.
   * If an entry with the same path hash exists from the left source, it will
   * be updated to SAME. Otherwise, it is marked as RIGHT_ONLY.
   *
   * @param relativePathHash the hash of the file's relative path
   * @param relativePath the relative path of the file
   * @param size the size of the file in bytes
   * @param modified the last modification timestamp in milliseconds
   * @param flag the file type flag representing attributes such as file or directory
   */
  @Override
  public void addRight(long relativePathHash,
                       String relativePath,
                       long size,
                       long modified,
                       byte flag) {

    Integer idx = index.get(relativePathHash);

    if (idx == null) {
      buffer.add(
          relativePathHash,
          relativePath,
          size,
          modified,
          flag,
          FileStatus.RIGHT_ONLY.getValue()
      );
      return;
    }

    buffer.updateStatus(idx, FileStatus.SAME.getValue());
    index.remove(relativePathHash);
  }

  /**
   * Returns an immutable snapshot of the current diff state.
   *
   * @return a BufferSnapshot representing the current state of the diff
   */
  @Override
  public BufferSnapshot snapshot() {
    return buffer.freeze();
  }

  public void reset() {
    buffer.clear();
    index.clear();
  }

}