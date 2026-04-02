package jmh;

import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.cunsumer.impl.NoOpReportConsumer;
import com.hawk.fast_file_sync.exception.OperationCancelledException;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.model.CancellationToken;
import com.hawk.fast_file_sync.model.SimpleCancellationToken;
import com.hawk.fast_file_sync.sync.SyncEngine;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import com.hawk.fast_file_sync.sync.strategy.StreamSyncStrategy;
import org.openjdk.jmh.annotations.*;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(1)
@State(Scope.Thread)
public class SyncEngineBenchmark {

  private SyncEngine engine;
  private BufferSnapshot snapshot;
  private StreamSyncStrategy strategy;
  private CancellationToken token;
  private Path leftRoot, rightRoot, targetRoot;

  @Setup(Level.Iteration)
  public void setup() {

    ReportConsumer consumer = new NoOpReportConsumer();

    ErrorHandlingPolicy errorPolicy = e -> {};
    engine = new SyncEngine(consumer, errorPolicy);

    int size = 1000;
    long[] hashes = new long[size];
    String[] paths = new String[size];
    long[] fileSizes = new long[size];
    long[] modified = new long[size];
    byte[] flags = new byte[size];
    byte[] statuses = new byte[size];

    for (int i = 0; i < size; i++) {
      hashes[i] = i;
      paths[i] = "file" + i + ".txt";
      fileSizes[i] = 1024;
      modified[i] = System.currentTimeMillis();
      flags[i] = 0;
      statuses[i] = 0;
    }

    snapshot = new BufferSnapshot(hashes, paths, fileSizes, modified, flags, statuses, size);
    strategy = (s, i, l, r, t) -> {};
    token = new SimpleCancellationToken();
    leftRoot = Path.of("/left");
    rightRoot = Path.of("/right");
    targetRoot = Path.of("/target");
  }

  @Benchmark
  public void testProcess() throws OperationCancelledException {
    engine.process(leftRoot, rightRoot, targetRoot, snapshot, strategy, token);
  }
}