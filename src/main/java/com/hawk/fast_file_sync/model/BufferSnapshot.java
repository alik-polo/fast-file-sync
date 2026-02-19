package com.hawk.fast_file_sync.model;

import java.util.Arrays;

/**
 * Immutable snapshot of file metadata captured from an EntryBuffer.
 */
public class BufferSnapshot {
  private final long[] relativePathHashes;
  private final String[] relativePaths;
  private final long[] sizes;
  private final long[] modified;
  private final byte[] flags;
  private final byte[] statuses;
  private final int size;

  /**
   * Creates a snapshot by copying the provided arrays up to the specified size.
   * The resulting snapshot is immutable and independent of the original buffer.
   *
   * @param relativePathHashes array of relative path hashes
   * @param relativePaths array of relative path strings
   * @param sizes array of file sizes
   * @param modified array of last modification timestamps
   * @param flags array of file type flags
   * @param statuses array of file status flags
   * @param size number of valid entries to copy
   */
  public BufferSnapshot(long[] relativePathHashes,
                        String[] relativePaths,
                        long[] sizes,
                        long[] modified,
                        byte[] flags,
                        byte[] statuses,
                        int size) {
    this.relativePathHashes = Arrays.copyOf(relativePathHashes, size);
    this.relativePaths = Arrays.copyOf(relativePaths, size);
    this.sizes = Arrays.copyOf(sizes, size);
    this.modified = Arrays.copyOf(modified, size);
    this.flags = Arrays.copyOf(flags, size);
    this.statuses = Arrays.copyOf(statuses, size);
    this.size = size;
  }

  public long[] getRelativePathHashes() {
    return relativePathHashes;
  }

  public String[] getRelativePaths() {
    return relativePaths;
  }

  public long[] getSizes() {
    return sizes;
  }

  public long[] getModified() {
    return modified;
  }

  public byte[] getFlags() {
    return flags;
  }

  public byte[] getStatuses() {
    return statuses;
  }

  public int getSnapshotSize() {
    return size;
  }

  public long getRelativePathHash(int i) {
    return relativePathHashes[i];
  }

  public String getRelativePath(int i) {
    return relativePaths[i];
  }

  public long getSize(int i) {
    return sizes[i];
  }

  public long getModified(int i) {
    return modified[i];
  }

  public byte getFlag(int i) {
    return flags[i];
  }

  public byte getStatus(int i) {
    return statuses[i];
  }

}
