package com.hawk.fastfilesync.enums;

/**
 * Defines the available options for resolving file conflicts.
 */
public enum ConflictOption {
  SAVE_BIGGEST,
  SAVE_SMALLEST,
  SAVE_LATEST_MODIFIED,
  SAVE_PREVIOUS_MODIFIED,
  SAVE_LEFT,
  SAVE_RIGHT;
}