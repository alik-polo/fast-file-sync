package com.hawk.fastfilesync.buffer;

import com.hawk.fastfilesync.model.BufferSnapshot;
import java.util.Arrays;

/**
 * Mutable buffer that stores file metadata in parallel primitive arrays.
 */
public class EntryBuffer {
  private long[] relativePathHashes;
  private String[] relativePaths;
  private long[] sizes;
  private long[] modified;
  private byte[] flags;
  private byte[] statuses;

  private int bufferSize;

  /**
   * Creates a new EntryBuffer with the specified initial capacity.
   *
   * @param initCapacity the initial number of entries the buffer can hold
   */
  public EntryBuffer(int initCapacity) {
    relativePathHashes = new long[initCapacity];
    relativePaths = new String[initCapacity];
    sizes = new long[initCapacity];
    modified = new long[initCapacity];
    flags = new byte[initCapacity];
    statuses = new byte[initCapacity];
    bufferSize = 0;
  }

  /**
   * Adds a new entry to the buffer.
   * Automatically grows the internal storage if needed.
   *
   * @param relativePathHash the hash of the relative path
   * @param relativePath the relative path string
   * @param size the file size in bytes
   * @param modified the last modification timestamp in milliseconds
   * @param flag the file type flag
   * @param status the file status flag
   */
  public void add(long relativePathHash,
                  String relativePath,
                  long size,
                  long modified,
                  byte flag,
                  byte status) {
    ensureCapacity();

    int i = bufferSize++;

    relativePathHashes[i] = relativePathHash;
    relativePaths[i] = relativePath;
    sizes[i] = size;
    this.modified[i] = modified;
    flags[i] = flag;
    statuses[i] = status;
  }

  /**
   * Updates the status value of an existing entry.
   *
   * @param idx the index of the entry
   * @param status the new status value
   */
  public void updateStatus(int idx, byte status) {
    statuses[idx] = status;
  }

  /**
   * Returns the size of the entry at the specified index.
   *
   * @param i the index of the entry
   * @return the size of the entry in bytes
   */
  public long size(int i) {
    return sizes[i];
  }

  /**
   * Clears the buffer, resetting its size to zero.
   * Existing data is not removed from internal arrays but will be overwritten.
   */
  public int size() {
    return bufferSize;
  }

  /**
   * Clears the buffer, resetting its size to zero.
   * Existing data is not removed from internal arrays but will be overwritten.
   */
  public void clear() {
    bufferSize = 0;
  }

  /**
   * Creates an immutable snapshot of the current buffer state.
   *
   * @return a BufferSnapshot representing the current data
   */
  public BufferSnapshot freeze() {
    return new BufferSnapshot(
        relativePathHashes,
        relativePaths,
        sizes,
        modified,
        flags,
        statuses,
        bufferSize
    );
  }

  private void ensureCapacity() {
    if (bufferSize >= relativePathHashes.length) {
      grow();
    }
  }

  private void grow() {
    int newCapacity = relativePathHashes.length + (relativePathHashes.length >> 1);

    relativePathHashes = Arrays.copyOf(relativePathHashes, newCapacity);
    relativePaths = Arrays.copyOf(relativePaths, newCapacity);
    sizes = Arrays.copyOf(sizes, newCapacity);
    modified = Arrays.copyOf(modified, newCapacity);
    flags = Arrays.copyOf(flags, newCapacity);
    statuses = Arrays.copyOf(statuses, newCapacity);
  }

}
