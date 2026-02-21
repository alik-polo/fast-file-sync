package com.hawk.fast_file_sync.diff.factory;

import com.hawk.fast_file_sync.buffer.EntryBuffer;
import com.hawk.fast_file_sync.diff.StreamDiffStrategy;
import com.hawk.fast_file_sync.diff.impl.DeepStreamDiffStrategy;
import com.hawk.fast_file_sync.diff.impl.FastStreamDiffStrategy;
import com.hawk.fast_file_sync.enums.DiffOption;
import com.hawk.fast_file_sync.index.Index;

public class StreamDiffFactory {

  private StreamDiffFactory() {
  }

  public static StreamDiffStrategy getStrategy(DiffOption option,
                                               EntryBuffer buffer,
                                               Index index) {

    if (option == null) {
      throw new IllegalArgumentException();
    }

    return switch (option) {
      case FAST -> new FastStreamDiffStrategy(buffer, index);
      case DEEP -> new DeepStreamDiffStrategy(buffer, index);
    };
  }

}
