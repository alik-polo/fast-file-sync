package com.hawk.fast_file_sync.sync.policy.impl;

import com.hawk.fast_file_sync.exception.TraversalException;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Error handling policy that records all errors but continues processing.
 */
public class BestEffortPolicy implements ErrorHandlingPolicy {
  private final List<IOException> errors = new ArrayList<>();

  /**
   * Records the given exception without interrupting processing.
   *
   * @param e the IOException to record
   */
  @Override
  public void handle(IOException e) throws TraversalException {
    errors.add(e);
  }

  public List<IOException> getErrors() {
    return errors;
  }
}
