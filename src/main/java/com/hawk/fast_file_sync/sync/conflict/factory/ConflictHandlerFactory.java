package com.hawk.fast_file_sync.sync.conflict.factory;

import com.hawk.fast_file_sync.enums.ConflictOption;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.conflict.impl.ConflictHandlerBiggestMode;
import com.hawk.fast_file_sync.sync.conflict.impl.ConflictHandlerLatestModifiedMode;
import com.hawk.fast_file_sync.sync.conflict.impl.ConflictHandlerLeftMode;
import com.hawk.fast_file_sync.sync.conflict.impl.ConflictHandlerPreviousModifiedMode;
import com.hawk.fast_file_sync.sync.conflict.impl.ConflictHandlerRightMode;
import com.hawk.fast_file_sync.sync.conflict.impl.ConflictHandlerSmallestMode;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;

/**
 * Factory for creating ConflictHandler instances
 * based on the specified conflict option.
 */
public class ConflictHandlerFactory {

  /**
   * Factory for creating ConflictHandler instances
   * based on the specified conflict option.
   */
  private ConflictHandlerFactory() {
  }

  /**
   * Returns a ConflictHandler implementation
   * corresponding to the given conflict option.
   *
   * @param option the conflict resolution option
   * @param syncExecutor the executor used for file synchronization
   * @return a ConflictHandler matching the specified option
   * @throws IllegalArgumentException if the option is null
   */
  public static ConflictHandler getConflictMode(ConflictOption option,
                                                SyncExecutor syncExecutor) {

    if (option == null) {
      throw new IllegalArgumentException();
    }

    return switch (option) {
      case SAVE_BIGGEST -> new ConflictHandlerBiggestMode(syncExecutor);
      case SAVE_SMALLEST -> new ConflictHandlerSmallestMode(syncExecutor);
      case SAVE_LATEST_MODIFIED -> new ConflictHandlerLatestModifiedMode(syncExecutor);
      case SAVE_PREVIOUS_MODIFIED -> new ConflictHandlerPreviousModifiedMode(syncExecutor);
      case SAVE_LEFT -> new ConflictHandlerLeftMode(syncExecutor);
      case SAVE_RIGHT -> new ConflictHandlerRightMode(syncExecutor);
    };
  }
}
