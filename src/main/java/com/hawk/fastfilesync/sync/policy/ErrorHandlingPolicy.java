package com.hawk.fastfilesync.sync.policy;

import com.hawk.fastfilesync.exception.TraversalException;
import java.io.IOException;

/**
 * Policy interface for handling I/O errors during synchronization.
 */
public interface ErrorHandlingPolicy {

  /**
   * Handles an I/O exception according to the policy.
   *
   * @param e the IOException to handle
   * @throws TraversalException if the policy requires propagating the exception
   */
  void handle(IOException e) throws TraversalException;
}
