package unit.sync;

import com.hawk.fastfilesync.enums.FileStatus;
import com.hawk.fastfilesync.enums.FileType;
import com.hawk.fastfilesync.model.BufferSnapshot;
import com.hawk.fastfilesync.sync.conflict.ConflictHandler;
import com.hawk.fastfilesync.sync.executor.SyncExecutor;
import com.hawk.fastfilesync.sync.strategy.StreamSyncStrategy;
import com.hawk.fastfilesync.sync.strategy.impl.NewStreamSyncStrategy;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NewStreamSyncStrategyTests {
  private final Path left = Path.of("/left");
  private final Path right = Path.of("/right");
  private final Path target = Path.of("/new");

  @Mock
  SyncExecutor executor;
  @Mock
  ConflictHandler conflictHandler;
  @Mock
  BufferSnapshot snapshot;

  StreamSyncStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new NewStreamSyncStrategy(executor, conflictHandler);
  }

  @Test
  void handle_shouldDelegateToExecutor_whenLeftOnly() throws Exception {
    String file = "file.txt";

    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.LEFT_ONLY.getValue());
    Mockito.when(snapshot.getRelativePath(0))
        .thenReturn(file);
    Mockito.when(snapshot.getFlag(0))
        .thenReturn(FileType.REGULAR_FILE.getValue());

    strategy.handle(snapshot, 0, left, right, target);

    Mockito.verify(executor).execute(
        left.resolve(file),
        target.resolve(file),
        FileType.REGULAR_FILE.getValue()
    );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldDelegateToExecutor_whenSame() throws Exception {
    String file = "file.txt";

    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.SAME.getValue());
    Mockito.when(snapshot.getRelativePath(0))
        .thenReturn(file);
    Mockito.when(snapshot.getFlag(0))
        .thenReturn(FileType.REGULAR_FILE.getValue());

    strategy.handle(snapshot, 0, left, right, target);

    Mockito.verify(executor).execute(
        left.resolve(file),
        target.resolve(file),
        FileType.REGULAR_FILE.getValue()
    );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldDelegateToExecutor_whenRightOnly() throws Exception {
    String file = "file.txt";

    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.RIGHT_ONLY.getValue());
    Mockito.when(snapshot.getRelativePath(0))
        .thenReturn(file);
    Mockito.when(snapshot.getFlag(0))
        .thenReturn(FileType.REGULAR_FILE.getValue());

    strategy.handle(snapshot, 0, left, right, target);

    Mockito.verify(executor).execute(
        right.resolve(file),
        target.resolve(file),
        FileType.REGULAR_FILE.getValue()
    );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldDelegateToConflictHandler_whenConflict() throws Exception {
    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.CONFLICT.getValue());

    strategy.handle(snapshot, 0, left, right, target);

    Mockito.verify(conflictHandler)
        .handle(snapshot, 0, left, right, target);

    Mockito.verifyNoInteractions(executor);
  }

  @Test
  void handle_shouldThrowIOException_whenExecutorFails() throws Exception {
    String file = "file.txt";

    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.LEFT_ONLY.getValue());
    Mockito.when(snapshot.getRelativePath(0))
        .thenReturn(file);
    Mockito.when(snapshot.getFlag(0))
        .thenReturn(FileType.REGULAR_FILE.getValue());

    Mockito.doThrow(new IOException("I/O error"))
        .when(executor)
        .execute(
            left.resolve(file),
            target.resolve(file),
            FileType.REGULAR_FILE.getValue()
        );

    Assertions.assertThrows(
        IOException.class,
        () -> strategy.handle(snapshot, 0, left, right, target)
    );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldThrowIOException_whenConflictHandlerFails() throws Exception {
    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.CONFLICT.getValue());

    Mockito.doThrow(new IOException("I/O error"))
        .when(conflictHandler)
        .handle(snapshot, 0, left, right, target);

    Assertions.assertThrows(
        IOException.class,
        () -> strategy.handle(snapshot, 0, left, right, target)
    );

    Mockito.verifyNoInteractions(executor);
  }

}
