package unit;

import com.hawk.fast_file_sync.buffer.EntryBuffer;
import com.hawk.fast_file_sync.diff.StreamDiffStrategy;
import com.hawk.fast_file_sync.diff.impl.DeepStreamDiffStrategy;
import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.index.Index;
import com.hawk.fast_file_sync.index.impl.InMemoryIndex;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeepStreamDiffStrategyTest {

  @Test
  void shouldMarkAsLeftOnly() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addLeft(1L,"file.txt",100L,200L, (byte) 1);

    BufferSnapshot snapshot = strategy.snapshot();
    Assertions.assertEquals(1, snapshot.getSnapshotSize());
    Assertions.assertEquals(FileStatus.LEFT_ONLY.getValue(), snapshot.getStatus(0));
  }

  @Test
  void shouldMarkAsRightOnlyWhenNoLeftExists() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();
    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addRight(1L,"file.txt",100L,200L, (byte) 1);

    BufferSnapshot snapshot = strategy.snapshot();
    Assertions.assertEquals(1, snapshot.getSnapshotSize());
    Assertions.assertEquals(FileStatus.RIGHT_ONLY.getValue(), snapshot.getStatus(0));
  }

  @Test
  void shouldMarkAsSameWhenLeftExistsAndSizeEquals() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();

    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addLeft(1L,"file.txt",100,200, (byte) 1);

    BufferSnapshot snapshotAfterLeft = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterLeft.getSnapshotSize());
    Assertions.assertEquals(FileStatus.LEFT_ONLY.getValue(), snapshotAfterLeft.getStatuses()[0]);

    strategy.addRight(1L,"file.txt",100,200, (byte) 1);

    BufferSnapshot snapshotAfterRight = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterRight.getSnapshotSize());
    Assertions.assertEquals(FileStatus.SAME.getValue(), snapshotAfterRight.getStatuses()[0]);
  }

  @Test
  void shouldMarkAsSameWhenLeftExistsAndSizeNoEquals() {
    EntryBuffer buffer = new EntryBuffer(2);
    Index index = new InMemoryIndex();

    StreamDiffStrategy strategy = new DeepStreamDiffStrategy(buffer, index);

    strategy.addLeft(1L,"file.txt",100,200, (byte) 1);

    BufferSnapshot snapshotAfterLeft = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterLeft.getSnapshotSize());
    Assertions.assertEquals(FileStatus.LEFT_ONLY.getValue(), snapshotAfterLeft.getStatuses()[0]);

    strategy.addRight(1L,"file.txt",200,200, (byte) 1);

    BufferSnapshot snapshotAfterRight = strategy.snapshot();
    Assertions.assertEquals(1, snapshotAfterRight.getSnapshotSize());
    Assertions.assertEquals(FileStatus.CONFLICT.getValue(), snapshotAfterRight.getStatuses()[0]);
  }

}
