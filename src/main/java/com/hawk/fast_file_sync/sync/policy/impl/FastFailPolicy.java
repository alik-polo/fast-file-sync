package com.hawk.fast_file_sync.sync.policy.impl;

import com.hawk.fast_file_sync.exception.TraversalException;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import java.io.IOException;

/**
 * Error handling policy that fails immediately on the first encountered exception.
 */
public class FastFailPolicy implements ErrorHandlingPolicy {

  /**
   * Error handling policy that fails immediately on the first encountered exception.
   */
  @Override
  public void handle(IOException e) throws TraversalException {
    throw new TraversalException(e.getMessage(), e);
  }
}
