package com.hawk.fastfilesync.enums;

/**
 * Defines statuses of files that can be encountered during scanning.
 */
public enum FileStatus {
  NONE((byte) -1),
  SAME((byte) 0),
  LEFT_ONLY((byte) 1),
  RIGHT_ONLY((byte) 2),
  CONFLICT((byte) 3);

  private final byte value;

  FileStatus(byte value) {
    this.value = value;
  }

  public byte getValue() {
    return value;
  }

  /**
   * Returns the corresponding FileStatus for a given byte value.
   * If the value does not match any known status, NONE is returned.
   *
   * @param value the byte value representing a file status
   * @return the corresponding FileStatus, or NONE if not found
   */
  public static FileStatus fromValue(byte value) {
    for (FileStatus status : values()) {
      if (status.value == value) {
        return status;
      }
    }
    return NONE;
  }
}
