package com.hawk.fast_file_sync.index.impl;

import com.hawk.fast_file_sync.index.Index;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * In-memory implementation of Index backed by a HashMap.
 * Provides fast key-value lookup and modification within a single JVM instance.
 */
public class InMemoryIndex implements Index {
  private final Map<Long, Integer> index;

  /**
   * Creates a new empty in-memory index.
   */
  public InMemoryIndex() {
    index = new HashMap<>();
  }

  @Override
  public void add(Long key, Integer value) {
    index.put(key, value);
  }

  @Override
  public Integer get(Long key) {
    return index.get(key);
  }

  @Override
  public void remove(Long key) {
    index.remove(key);
  }

  @Override
  public Set<Long> keys() {
    return index.keySet();
  }

  @Override
  public void clear() {
    index.clear();
  }
}
