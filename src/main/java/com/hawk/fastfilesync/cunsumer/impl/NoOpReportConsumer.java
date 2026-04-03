package com.hawk.fastfilesync.cunsumer.impl;

import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.model.BufferSnapshot;

/**
 * A {@link ReportConsumer} implementation that performs no operations.
 * All methods are empty and do not produce any output or side effects.
 */
public class NoOpReportConsumer implements ReportConsumer {

  /**
   * Does nothing for accepted entries.
   *
   * @param snapshot the buffer snapshot containing the entry
   * @param index the index of the entry in the snapshot
   */
  @Override
  public void accept(BufferSnapshot snapshot, int index) {}

  /**
   * Does nothing for failed entries.
   *
   * @param snapshot the buffer snapshot containing the entry
   * @param index the index of the entry in the snapshot
   */
  @Override
  public void fail(BufferSnapshot snapshot, int index) {}

  /**
   * Does nothing for informational messages.
   *
   * @param msg the message
   */
  @Override
  public void info(String msg) {}
}