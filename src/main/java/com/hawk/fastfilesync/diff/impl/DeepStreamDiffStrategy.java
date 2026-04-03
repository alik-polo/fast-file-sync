package com.hawk.fastfilesync.diff.impl;

import com.hawk.fastfilesync.buffer.EntryBuffer;
import com.hawk.fastfilesync.diff.StreamDiffStrategy;
import com.hawk.fastfilesync.enums.FileStatus;
import com.hawk.fastfilesync.index.Index;
import com.hawk.fastfilesync.model.BufferSnapshot;

/**
 * Deep implementation of StreamDiffStrategy that performs a detailed comparison
 * between two sets of file metadata.
 */
public class DeepStreamDiffStrategy implements StreamDiffStrategy {
  private final EntryBuffer buffer;
  private final Index index;

  /**
   * Constructs a DeepStreamDiffStrategy with the given buffer and index.
   *
   * @param buffer the EntryBuffer used to store file entries
   * @param index the Index used to map path hashes to buffer positions
   */
  public DeepStreamDiffStrategy(EntryBuffer buffer, Index index) {
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
   * If an entry with the same path hash exists in the left source, it compares
   * the sizes of the files. If the sizes match, the entry is marked as SAME.
   * If the sizes differ, the entry is marked as CONFLICT. If no left entry exists,
   * it is marked as RIGHT_ONLY.
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

    if (buffer.size(idx) == size) {
      buffer.updateStatus(idx, FileStatus.SAME.getValue());
    } else {
      buffer.updateStatus(idx, FileStatus.CONFLICT.getValue());
    }
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

  /**
   * Resets the strategy by clearing the entry buffer and index.
   */
  public void reset() {
    buffer.clear();
    index.clear();
  }
}
