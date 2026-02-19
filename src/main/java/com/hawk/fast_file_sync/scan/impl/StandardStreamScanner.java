package com.hawk.fast_file_sync.scan.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.hawk.fast_file_sync.cunsumer.ScanConsumer;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.enums.FileType;
import com.hawk.fast_file_sync.exception.OperationCancelledException;
import com.hawk.fast_file_sync.exception.TraversalException;
import com.hawk.fast_file_sync.filter.ScanFilter;
import com.hawk.fast_file_sync.model.CancellationToken;
import com.hawk.fast_file_sync.scan.FileScanner;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Standard implementation of FileScanner that recursively traverses a directory tree
 */
public class StandardStreamScanner implements FileScanner {
  private static final HashFunction HASH = Hashing.murmur3_128();
  private final List<ScanFilter> filters;

  /**
   * Creates a StandardStreamScanner with the specified list of filters.
   *
   * @param filters the list of ScanFilter to apply during scanning
   */
  public StandardStreamScanner(List<ScanFilter> filters) {
    this.filters = filters == null ? List.of() : filters;
  }

  /**
   * Creates a StandardStreamScanner without any filters.
   */
  public StandardStreamScanner() {
    this(List.of());
  }

  /**
   * Scans the specified root directory and passes metadata of each file and directory
   * to the provided consumer.
   * The scan can be interrupted through the cancellation token.
   *
   * @param root the root directory to start scanning from
   * @param consumer the consumer that handles discovered files and directories
   * @param cancellationToken a token that allows the scan to be cancelled
   * @throws TraversalException if an I/O error occurs during traversal
   * @throws OperationCancelledException if the scan is cancelled before completion
   */
  @Override
  public void scan(Path root, ScanConsumer consumer, CancellationToken cancellationToken)
      throws TraversalException, OperationCancelledException {

    FileVisitor visitor = new FileVisitor(root, consumer, cancellationToken, filters);

    try {
      Files.walkFileTree(
          root,
          EnumSet.noneOf(FileVisitOption.class),
          Integer.MAX_VALUE,
          visitor
      );
    } catch (OperationCancelledException e) {
      throw e;
    } catch (IOException e) {
      throw new TraversalException("Traversal failed for root: " + root, e);
    }
  }

  /**
   * Internal file visitor that processes each file and directory,
   */
  static final class FileVisitor extends SimpleFileVisitor<Path> {
    private final Path root;
    private final ScanConsumer consumer;
    private final CancellationToken cancellationToken;
    private final List<ScanFilter> filters;
    private final Set<Object> visited;

    FileVisitor(Path root,
                ScanConsumer consumer,
                CancellationToken cancellationToken,
                List<ScanFilter> filters) {
      this.root = root;
      this.consumer = consumer;
      this.cancellationToken = cancellationToken;
      this.filters = filters;
      this.visited = new HashSet<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
        throws IOException {
      cancellationToken.throwIfCancelled();

      for (ScanFilter filter : filters) {
        if (!filter.accept(file, attrs)) {
          return FileVisitResult.CONTINUE;
        }
      }

      process(file, attrs);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
      cancellationToken.throwIfCancelled();

      if (dir.equals(root)) {
        return FileVisitResult.CONTINUE;
      }

      for (ScanFilter filter : filters) {
        if (!filter.accept(dir, attrs)) {
          return FileVisitResult.SKIP_SUBTREE;
        }
      }

      Object key = attrs.fileKey();
      if (key != null && visited.contains(key)) {
        return FileVisitResult.SKIP_SUBTREE;
      }
      if (key != null) {
        visited.add(key);
      }

      process(dir, attrs);
      return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
        throws IOException {
      return FileVisitResult.CONTINUE;
    }

    private void process(Path current,
                         BasicFileAttributes attrs) {
      String relative = root
          .relativize(current)
          .toString()
          .replace(File.separatorChar, '/');

      long relativeHash = HASH.hashString(relative, StandardCharsets.UTF_8).asLong();
      long size = attrs.size();
      long modified = attrs.lastModifiedTime().toMillis();
      byte flag = defineType(attrs);

      consumer.accept(
          relativeHash,
          relative,
          size,
          modified,
          flag
      );
    }

    private byte defineType(BasicFileAttributes attrs) {
      if (attrs.isDirectory()) {
        return FileType.DIRECTORY.getValue();
      } else if (attrs.isRegularFile()) {
        return FileType.REGULAR_FILE.getValue();
      } else if (attrs.isSymbolicLink()) {
        return FileType.SYMLINK.getValue();
      } else if (attrs.isOther()) {
        return FileType.OTHER.getValue();
      }
      return FileType.UNKNOWN.getValue();
    }
  }

}