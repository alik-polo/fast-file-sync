package com.hawk.fastfilesync.exception;

/**
 * Exception thrown when an operation is cancelled via a CancellationToken.
 */
public class OperationCancelledException extends RuntimeException {

  /**
   * Constructs a new OperationCancelledException with the specified message.
   *
   * @param message the detail message explaining why the operation was cancelled
   */
  public OperationCancelledException(String message) {
    super(message);
  }
}
