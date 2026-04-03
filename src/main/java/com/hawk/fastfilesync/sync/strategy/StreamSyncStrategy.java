package com.hawk.fastfilesync.sync.strategy;

import com.hawk.fastfilesync.model.BufferSnapshot;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Strategy for synchronizing a single entry based on buffer snapshot data.
 */
public interface StreamSyncStrategy {

  /**
   * Processes an entry identified by the given index and performs
   * synchronization between source and target roots.
   *
   * @param snapshot the buffer snapshot containing entry metadata
   * @param index the index of the entry to process
   * @param leftRoot the left source root directory
   * @param rightRoot the right source root directory
   * @param targetRoot the target root directory
   * @throws IOException if an I/O error occurs during synchronization
   */
  void handle(BufferSnapshot snapshot,
              int index,
              Path leftRoot,
              Path rightRoot,
              Path targetRoot) throws IOException;

}