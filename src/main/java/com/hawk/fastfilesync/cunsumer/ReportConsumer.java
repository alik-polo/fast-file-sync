package com.hawk.fastfilesync.cunsumer;

import com.hawk.fastfilesync.model.BufferSnapshot;

/**
 * Consumer interface for handling reports during file scanning and synchronization.
 * Provides methods for accepting, failing, and reporting information about
 * individual file entries or general messages.
 */
public interface ReportConsumer {

  /**
   * Accepts a successfully processed entry from the buffer snapshot.
   *
   * @param snapshot the buffer snapshot containing the entry
   * @param index the index of the entry in the snapshot
   */
  void accept(BufferSnapshot snapshot,
              int index);

  /**
   * Accepts a successfully processed entry from the buffer snapshot.
   *
   * @param snapshot the buffer snapshot containing the entry
   * @param index the index of the entry in the snapshot
   */
  void fail(BufferSnapshot snapshot,
            int index);

  /**
   * Reports a general informational message.
   *
   * @param msg the message to report
   */
  void info(String msg);

  /**
   * Clears any internal state or messages.
   * Default implementation does nothing.
   */
  default void clear() {
  }

}
