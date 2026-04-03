package unit.diff;

import com.hawk.fastfilesync.buffer.EntryBuffer;
import com.hawk.fastfilesync.diff.StreamDiffStrategy;
import com.hawk.fastfilesync.diff.impl.FastStreamDiffStrategy;
import com.hawk.fastfilesync.enums.FileStatus;
import com.hawk.fastfilesync.enums.FileType;
import com.hawk.fastfilesync.index.Index;
import com.hawk.fastfilesync.index.impl.InMemoryIndex;
import com.hawk.fastfilesync.model.BufferSnapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FastStreamDiffStrategyTests {

  @Test
  void addLeft_markLeftOnly() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new FastStreamDiffStrategy(buffer, index);

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
    StreamDiffStrategy strategy = new FastStreamDiffStrategy(buffer, index);

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
  void addRight_markSame_whenLeftExists() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new FastStreamDiffStrategy(buffer, index);

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
}
