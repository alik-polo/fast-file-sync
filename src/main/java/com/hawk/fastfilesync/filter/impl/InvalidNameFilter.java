package com.hawk.fastfilesync.filter.impl;

import com.hawk.fastfilesync.filter.ScanFilter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Normalizer;

/**
 * Filter that excludes files or directories whose names cannot be
 * correctly encoded and decoded with the specified character set.
 */
public class InvalidNameFilter implements ScanFilter {
  private final Charset charset;

  /**
   * Creates an InvalidNameFilter with the specified charset.
   *
   * @param charset the character set to use for name validation
   */
  public InvalidNameFilter(Charset charset) {
    this.charset = charset;
  }

  /**
   * Creates an InvalidNameFilter using UTF-8 charset by default.
   */
  public InvalidNameFilter() {
    this(StandardCharsets.UTF_8);
  }

  /**
   * Determines whether the given file or directory should be accepted.
   * Names that cannot be round-trip encoded and decoded using the specified
   * charset are rejected.
   *
   * @param path the path to the file or directory being tested
   * @param attrs the file attributes of the path
   * @return true if the name is valid in the specified charset, false otherwise
   */
  @SuppressWarnings("checkstyle:AvoidEscapedUnicodeCharacters")
  @Override
  public boolean accept(Path path, BasicFileAttributes attrs) {
    String name = path.getFileName().toString();

    if (name.trim().isEmpty()) {
      return false;
    }

    if (name.contains("\uFFFD") || name.contains("\0")) {
      return false;
    }

    byte[] bytes = name.getBytes(charset);
    String decoded = new String(bytes, charset);
    if (!decoded.equals(name)) {
      return false;
    }

    String normalized = Normalizer.normalize(name, Normalizer.Form.NFC);

    if (normalized.matches(".*[<>:\"/\\\\|?*].*")) {
      return false;
    }

    return !normalized.contains("\uF000");
  }

}