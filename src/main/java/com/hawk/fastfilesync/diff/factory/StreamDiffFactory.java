package com.hawk.fastfilesync.diff.factory;

import com.hawk.fastfilesync.buffer.EntryBuffer;
import com.hawk.fastfilesync.diff.StreamDiffStrategy;
import com.hawk.fastfilesync.diff.impl.DeepStreamDiffStrategy;
import com.hawk.fastfilesync.diff.impl.FastStreamDiffStrategy;
import com.hawk.fastfilesync.enums.DiffOption;
import com.hawk.fastfilesync.index.Index;

/**
 * Factory class for creating {@link StreamDiffStrategy} instances based on a {@link DiffOption}.
 * Provides a single static method to obtain the appropriate diff strategy.
 */
public class StreamDiffFactory {

  private StreamDiffFactory() {
  }

  /**
   * Returns a {@link StreamDiffStrategy} corresponding to the given diff option.
   *
   * @param option the difference calculation option
   * @param buffer the entry buffer to store scanned file metadata
   * @param index the index used for tracking file entries
   * @return a configured StreamDiffStrategy instance
   * @throws IllegalArgumentException if the option is null
   */
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
