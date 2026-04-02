package com.hawk.fast_file_sync.cunsumer.impl;

import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.model.BufferSnapshot;

public class NoOpReportConsumer implements ReportConsumer {
  @Override
  public void accept(BufferSnapshot snapshot, int index) {
  }

  @Override
  public void fail(BufferSnapshot snapshot, int index) {
  }

  @Override
  public void info(String msg) {
  }
}
