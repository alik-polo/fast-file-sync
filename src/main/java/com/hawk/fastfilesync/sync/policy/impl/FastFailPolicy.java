package com.hawk.fastfilesync.sync.policy.impl;

import com.hawk.fastfilesync.exception.TraversalException;
import com.hawk.fastfilesync.sync.policy.ErrorHandlingPolicy;
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
