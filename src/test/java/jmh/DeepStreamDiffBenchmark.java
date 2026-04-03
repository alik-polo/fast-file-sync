package jmh;

import com.hawk.fastfilesync.buffer.EntryBuffer;
import com.hawk.fastfilesync.diff.impl.DeepStreamDiffStrategy;
import com.hawk.fastfilesync.index.Index;
import com.hawk.fastfilesync.index.impl.InMemoryIndex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(1)
@State(Scope.Thread)
public class DeepStreamDiffBenchmark {

  private DeepStreamDiffStrategy diff;

  @Setup(Level.Iteration)
  public void setup() {
    EntryBuffer buffer = new EntryBuffer(100_000);
    Index index = new InMemoryIndex();
    diff = new DeepStreamDiffStrategy(buffer, index);

    for (int i = 0; i < 50_000; i++) {
      diff.addLeft(i, "file" + i, 100, 123, (byte) 1);
    }
  }

  @Benchmark
  public void testAddRightSame() {
    diff.reset();
    for (int i = 0; i < 50_000; i++) {
      diff.addRight(i, "file" + i, 100, 123, (byte) 1);
    }
  }
}

