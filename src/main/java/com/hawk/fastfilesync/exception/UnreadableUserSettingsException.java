package com.hawk.fastfilesync.exception;

/**
 * Exception thrown when user-provided settings are invalid or cannot be read.
 */
public class UnreadableUserSettingsException extends RuntimeException {

  /**
   * Creates a new exception with the specified detail message.
   *
   * @param msg the detail message
   */
  public UnreadableUserSettingsException(String msg) {
    super(msg);
  }
}
