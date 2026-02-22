package com.hawk.fast_file_sync.sync.conflict;

import com.hawk.fast_file_sync.model.BufferSnapshot;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Handles conflicts that occur during file synchronization.
 */
public interface ConflictHandler {

  /**
   * Resolves a conflict for the entry at the given index.
   *
   * @param snapshot the buffer snapshot containing entry metadata
   * @param index the index of the conflicting entry
   * @param leftRoot the left source root path
   * @param rightRoot the right source root path
   * @param targetRoot the target root path
   */
  void handle(BufferSnapshot snapshot,
              int index,
              Path leftRoot,
              Path rightRoot,
              Path targetRoot) throws IOException;

}
