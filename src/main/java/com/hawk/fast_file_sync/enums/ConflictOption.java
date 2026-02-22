package com.hawk.fast_file_sync.enums;

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