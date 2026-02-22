package com.hawk.fast_file_sync.sync;

import com.hawk.fast_file_sync.exception.OperationCancelledException;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.model.CancellationToken;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Engine for synchronizing files between multiple sources and a target.
 */
public class SyncEngine {
  private final ErrorHandlingPolicy errorPolicy;

  /**
   * Creates a SyncEngine with the specified error handling policy.
   *
   * @param errorPolicy the policy used to handle I/O errors during synchronization
   */
  public SyncEngine(ErrorHandlingPolicy errorPolicy) {
    this.errorPolicy = errorPolicy;
  }

  /**
   * Processes a buffer snapshot and synchronizes files according to the given strategy.
   * Iterates over all entries in the snapshot, applying the strategy and handling errors.
   *
   * @param leftRoot the left source root directory
   * @param rightRoot the right source root directory
   * @param targetRoot the target root directory
   * @param snapshot the buffer snapshot containing file metadata
   * @param strategy the synchronization strategy to apply for each entry
   * @param cancellationToken a token that allows the process to be cancelled
   * @throws OperationCancelledException if the process is cancelled before completion
   */
  public void process(Path leftRoot,
                      Path rightRoot,
                      Path targetRoot,
                      BufferSnapshot snapshot,
                      StreamSyncStrategy strategy,
                      CancellationToken cancellationToken)
      throws OperationCancelledException {

    for (int i = 0; i < snapshot.getSnapshotSize(); i++) {
      try {
        cancellationToken.throwIfCancelled();
        strategy.handle(
            snapshot,
            i,
            leftRoot,
            rightRoot,
            targetRoot
        );
      } catch (IOException e) {
        errorPolicy.handle(e);
      }
    }
  }
}