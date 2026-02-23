package com.hawk.fast_file_sync.cunsumer;

import com.hawk.fast_file_sync.model.BufferSnapshot;

public interface ReportConsumer {

  void accept(BufferSnapshot snapshot,
              int index);

  void fail(BufferSnapshot snapshot,
            int index);

}
