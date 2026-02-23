package unit.diff;

import com.hawk.fast_file_sync.buffer.EntryBuffer;
import com.hawk.fast_file_sync.diff.StreamDiffStrategy;
import com.hawk.fast_file_sync.diff.impl.DeepStreamDiffStrategy;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.enums.FileType;
import com.hawk.fast_file_sync.index.Index;
import com.hawk.fast_file_sync.index.impl.InMemoryIndex;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeepStreamDiffStrategyTests {

  @Test
  void addLeft_markLeftOnly() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addLeft(
        1L,
        "file.txt",
        100L,
        200L,
        FileType.REGULAR_FILE.getValue()
    );

    BufferSnapshot snapshot = strategy.snapshot();

    Assertions.assertEquals(1, snapshot.getSnapshotSize());
    Assertions.assertEquals(
        FileStatus.LEFT_ONLY.getValue(), snapshot.getStatus(0)
    );
  }

  @Test
  void addRight_markRightOnly_whenLeftNotExists() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addRight(
        1L,
        "file.txt",
        100L,
        200L,
        FileType.REGULAR_FILE.getValue()
    );

    BufferSnapshot snapshot = strategy.snapshot();

    Assertions.assertEquals(1, snapshot.getSnapshotSize());
    Assertions.assertEquals(
        FileStatus.RIGHT_ONLY.getValue(), snapshot.getStatus(0)
    );
  }

  @Test
  void addRight_markSame_whenLeftExistsAndSizeNotEquals() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addLeft(
        1L,
        "file.txt",
        100L,
        200L,
        FileType.REGULAR_FILE.getValue()
    );

    BufferSnapshot snapshotAfterAddLeft = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterAddLeft.getSnapshotSize());
    Assertions.assertEquals(
        FileStatus.LEFT_ONLY.getValue(), snapshotAfterAddLeft.getStatus(0)
    );

    strategy.addRight(
        1L,
        "file.txt",
        100L,
        200L,
        FileType.REGULAR_FILE.getValue()
    );

    BufferSnapshot snapshotAfterAddRight = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterAddRight.getSnapshotSize());
    Assertions.assertEquals(
        FileStatus.SAME.getValue(), snapshotAfterAddRight.getStatus(0)
    );
  }

  @Test
  void addRight_markConflict_whenLeftExistsAndSizeEquals() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addLeft(
        1L,
        "file.txt",
        100L,
        200L,
        FileType.REGULAR_FILE.getValue()
    );

    BufferSnapshot snapshotAfterAddLeft = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterAddLeft.getSnapshotSize());
    Assertions.assertEquals(
        FileStatus.LEFT_ONLY.getValue(), snapshotAfterAddLeft.getStatus(0)
    );

    strategy.addRight(
        1L,
        "file.txt",
        50L,
        200L,
        FileType.REGULAR_FILE.getValue()
    );

    BufferSnapshot snapshotAfterAddRight = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterAddRight.getSnapshotSize());
    Assertions.assertEquals(
        FileStatus.CONFLICT.getValue(), snapshotAfterAddRight.getStatus(0)
    );
  }
}
