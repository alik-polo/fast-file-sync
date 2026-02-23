package unit.sync;

import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.enums.FileType;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.impl.LeftStreamSyncStrategy;
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
public class LeftStreamSyncStrategyTests {
  private final Path left = Path.of("/left");
  private final Path right = Path.of("/right");

  @Mock
  SyncExecutor executor;
  @Mock
  ConflictHandler conflictHandler;
  @Mock
  BufferSnapshot snapshot;

  StreamSyncStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new LeftStreamSyncStrategy(executor, conflictHandler);
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

    strategy.handle(snapshot, 0, left, right, left);

    Mockito
        .verify(executor)
        .execute(
            right.resolve(file),
            left.resolve(file),
            FileType.REGULAR_FILE.getValue()
        );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldDelegateToConflictHandler_whenConflict() throws Exception {
    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.CONFLICT.getValue());

    strategy.handle(snapshot, 0, left, right, left);

    Mockito
        .verify(conflictHandler)
        .handle(snapshot, 0, left, right, left);

    Mockito.verifyNoInteractions(executor);
  }

  @Test
  void handle_shouldThrowIOException_whenExecutorFails() throws Exception {
    String file = "file.txt";

    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.RIGHT_ONLY.getValue());
    Mockito.when(snapshot.getRelativePath(0))
        .thenReturn(file);
    Mockito.when(snapshot.getFlag(0))
        .thenReturn(FileType.REGULAR_FILE.getValue());

    Mockito.doThrow(new IOException("I/O error"))
        .when(executor)
        .execute(right.resolve(file), left.resolve(file), FileType.REGULAR_FILE.getValue());

    Assertions.assertThrows(
        IOException.class,
        () -> strategy.handle(snapshot, 0, left, right, left)
    );

    Mockito.verify(executor)
        .execute(
            right.resolve(file),
            left.resolve(file),
            FileType.REGULAR_FILE.getValue()
        );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldThrowIOException_whenConflictHandlerFails() throws Exception {
    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.CONFLICT.getValue());

    Mockito.doThrow(new IOException("I/O error"))
        .when(conflictHandler)
        .handle(snapshot, 0, left, right, left);

    Assertions.assertThrows(
        IOException.class,
        () -> strategy.handle(snapshot, 0, left, right, left)
    );

    Mockito
        .verify(conflictHandler)
        .handle(snapshot, 0, left, right, left);

    Mockito.verifyNoInteractions(executor);
  }

}
