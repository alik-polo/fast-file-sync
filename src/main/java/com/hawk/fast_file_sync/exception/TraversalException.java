package com.hawk.fast_file_sync.exception;

/**
 * Exception thrown when an error occurs during directory traversal.
 */
public class TraversalException extends RuntimeException {

  /**
   * Constructs a new TraversalException with the specified message and cause.
   *
   * @param message the detail message explaining the reason for the exception
   * @param e the underlying cause of the exception
   */
  public TraversalException(String message, Throwable e) {
    super(message, e);
  }
}
