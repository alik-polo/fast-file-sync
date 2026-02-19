package com.hawk.fast_file_sync.model;

import com.hawk.fast_file_sync.exception.OperationCancelledException;

/**
 * Simple implementation of CancellationToken that allows marking an operation as cancelled.
 */
public class SimpleCancellationToken implements CancellationToken {
  private volatile boolean cancelled = false;

  @Override
  public void cancel() {
    cancelled = true;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void throwIfCancelled() {
    if (cancelled) {
      throw new OperationCancelledException("Cancel");
    }
  }
}
