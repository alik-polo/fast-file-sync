package com.hawk.fastfilesync.enums;

/**
 * Defines types of files that can be encountered during scanning.
 */
public enum FileType {
  UNKNOWN((byte) -1),
  REGULAR_FILE((byte) 0),
  DIRECTORY((byte) 1),
  SYMLINK((byte) 2),
  OTHER((byte) 3);

  private final byte value;

  FileType(byte value) {
    this.value = value;
  }

  public byte getValue() {
    return value;
  }

  /**
   * Returns the corresponding FileType for a given byte value.
   * If the value does not match any known type, UNKNOWN is returned.
   *
   * @param value the byte value representing a file type
   * @return the corresponding FileType, or UNKNOWN if not found
   */
  public static FileType fromValue(byte value) {
    for (FileType type : values()) {
      if (type.value == value) {
        return type;
      }
    }
    return UNKNOWN;
  }
}
