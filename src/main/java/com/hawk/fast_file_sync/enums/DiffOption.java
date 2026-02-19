package com.hawk.fast_file_sync.enums;

/**
 * Defines the available options for performing a diff between file sets.
 */
public enum DiffOption {
  UNKNOWN((byte) -1),
  FAST((byte) 0),
  DEEP((byte) 1);

  private final byte value;

  DiffOption(byte value) {
    this.value = value;
  }

  /**
   * Returns the corresponding DiffOption for a given byte value.
   * If the value does not match any known option, UNKNOWN is returned.
   *
   * @param value the byte value representing a diff option
   * @return the corresponding DiffOption, or UNKNOWN if not found
   */
  public static DiffOption fromValue(byte value) {
    for (DiffOption option : values()) {
      if (option.value == value) {
        return option;
      }
    }
    return UNKNOWN;
  }
}
