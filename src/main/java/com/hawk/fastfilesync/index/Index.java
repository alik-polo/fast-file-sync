package com.hawk.fastfilesync.index;

import java.util.Set;

/**
 * Represents a key-value index used for fast lookup of integer values by long keys.
 */
public interface Index {

  /**
   * Adds or updates a value associated with the given key.
   *
   * @param key the key to store
   * @param value the value associated with the key
   */
  void add(Long key, Integer value);

  /**
   * Returns the value associated with the given key.
   *
   * @param key the key to look up
   * @return the associated value, or null if the key is not present
   */
  Integer get(Long key);

  /**
   * Removes the entry associated with the given key.
   *
   * @param key the key to remove
   */
  void remove(Long key);

  /**
   * Returns a set of all keys currently stored in the index.
   *
   * @return a set containing all keys
   */
  Set<Long> keys();

  /**
   * Removes all entries from the index.
   */
  void clear();
}