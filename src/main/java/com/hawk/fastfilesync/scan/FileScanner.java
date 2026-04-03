package com.hawk.fastfilesync.scan;

import com.hawk.fastfilesync.cunsumer.ScanConsumer;
import com.hawk.fastfilesync.exception.OperationCancelledException;
import com.hawk.fastfilesync.exception.TraversalException;
import com.hawk.fastfilesync.model.CancellationToken;
import java.nio.file.Path;

/**
 * Contract for scanning directories and processing their contents.
 */
public interface FileScanner {

  /**
   * Scans the given root directory.
   * The scan can be interrupted through the cancellation token.
   *
   * @param root the root directory to start scanning from
   * @param consumer the callback that handles each discovered file or directory
   * @param cancellationToken a token that allows the scan to be cancelled
   * @throws TraversalException if an error occurs during directory traversal
   * @throws OperationCancelledException if the scan is cancelled before completion
   */
  void scan(Path root,
            ScanConsumer consumer,
            CancellationToken cancellationToken)
      throws TraversalException, OperationCancelledException;

}
