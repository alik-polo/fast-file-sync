package unit.sync;

import com.hawk.fast_file_sync.enums.FileStatus;
import com.hawk.fast_file_sync.enums.FileType;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.sync.conflict.ConflictHandler;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import com.hawk.fast_file_sync.sync.strategy.impl.RightStreamSyncStrategy;
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
public class RightStreamSyncStrategyTests {
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
    strategy = new RightStreamSyncStrategy(executor, conflictHandler);
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

    strategy.handle(snapshot, 0, left, right, right);

    Mockito
        .verify(executor)
        .execute(
            left.resolve(file),
            right.resolve(file),
            FileType.REGULAR_FILE.getValue()
        );

    Mockito.verifyNoInteractions(conflictHandler);
  }

  @Test
  void handle_shouldDelegateToConflictHandler_whenConflict() throws Exception {
    Mockito.when(snapshot.getStatus(0))
        .thenReturn(FileStatus.CONFLICT.getValue());

    strategy.handle(snapshot, 0, left, right, right);

    Mockito
        .verify(conflictHandler)
        .handle(snapshot, 0, left, right, right);

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
        .execute(left.resolve(file), right.resolve(file), FileType.REGULAR_FILE.getValue());

    Assertions.assertThrows(
        IOException.class,
        () -> strategy.handle(snapshot, 0, left, right, right)
    );

    Mockito.verify(executor)
        .execute(
            left.resolve(file),
            right.resolve(file),
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
        .handle(snapshot, 0, left, right, right);

    Assertions.assertThrows(
        IOException.class,
        () -> strategy.handle(snapshot, 0, left, right, right)
    );

    Mockito
        .verify(conflictHandler)
        .handle(snapshot, 0, left, right, right);

    Mockito.verifyNoInteractions(executor);
  }

}
