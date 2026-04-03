package com.hawk.fastfilesync.model;

import com.hawk.fastfilesync.exception.OperationCancelledException;

/**
 * Represents a token that can be used to monitor and control cancellation of operations.
 */
public interface CancellationToken {

  /**
   * Checks whether the operation has been cancelled.
   *
   * @return true if the operation is cancelled, false otherwise
   */
  boolean isCancelled();

  /**
   * Throws an exception if the operation has been cancelled.
   *
   * @throws OperationCancelledException when the operation has been cancelled
   */
  void throwIfCancelled();

  /**
   * Marks the operation as cancelled.
   */
  void cancel();
}
