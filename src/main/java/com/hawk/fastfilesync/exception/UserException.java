package com.hawk.fastfilesync.exception;

/**
 * Exception thrown when an error occurs due to user actions or invalid input.
 */
public class UserException extends RuntimeException {

  /**
   * Constructs a new UserException with the specified detail message.
   *
   * @param message the detail message explaining the reason for the exception
   */
  public UserException(String message) {
    super(message);
  }

  /**
   * Constructs a new UserException with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the exception
   * @param cause the underlying cause of the exception
   */
  public UserException(String message, Throwable cause) {
    super(message, cause);
  }
}