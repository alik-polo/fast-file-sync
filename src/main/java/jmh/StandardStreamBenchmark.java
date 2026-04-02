package jmh;

import com.hawk.fast_file_sync.model.SimpleCancellationToken;
import com.hawk.fast_file_sync.scan.FileScanner;
import com.hawk.fast_file_sync.scan.impl.StandardStreamScanner;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(1)
@State(Scope.Thread)
public class StandardStreamBenchmark {

  public static void main(String[] args) throws Exception {
    FileScanner scanner = new StandardStreamScanner();

    Path root = Paths.get("test-data/huge-folder");

    long start = System.nanoTime();

    scanner.scan(
        root,
        (h, p, s, m, f) -> {},
        new SimpleCancellationToken()
    );

    long duration = System.nanoTime() - start;

    System.out.println("Scan time (ms): " + duration / 1_000_000);
  }
}