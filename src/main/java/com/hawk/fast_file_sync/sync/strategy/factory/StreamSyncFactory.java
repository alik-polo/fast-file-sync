package com.hawk.fast_file_sync.sync.strategy.factory;

import com.hawk.fast_file_sync.enums.SyncOption;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.impl.BothStreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.impl.LeftStreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.impl.NewStreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.impl.RightStreamSyncStrategy;

/**
 * Factory for creating StreamSyncStrategy instances
 * based on the specified synchronization option.
 */
public class StreamSyncFactory {

  /**
   * Prevents instantiation of the factory.
   */
  private StreamSyncFactory() {
  }

  /**
   * Returns a StreamSyncStrategy implementation
   * corresponding to the given synchronization option.
   *
   * @param option the synchronization mode
   * @param syncExecutor the executor used for file synchronization
   * @param conflictHandler the handler used to resolve conflicts
   * @return a StreamSyncStrategy matching the specified option
   * @throws IllegalArgumentException if the option is null
   */
  public static StreamSyncStrategy getStrategy(SyncOption option,
                                               SyncExecutor syncExecutor,
                                               ConflictHandler conflictHandler) {
    if (option == null) {
      throw new IllegalArgumentException();
    }

    return switch (option) {
      case LEFT -> new LeftStreamSyncStrategy(syncExecutor, conflictHandler);
      case RIGHT -> new RightStreamSyncStrategy(syncExecutor, conflictHandler);
      case NEW -> new NewStreamSyncStrategy(syncExecutor, conflictHandler);
      case BOTH -> new BothStreamSyncStrategy(syncExecutor, conflictHandler);
    };
  }
}
