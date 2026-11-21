
package benchmark;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StringConcat10Benchmark {

    private final String[] names = {"foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply", "waldo", "fred"};
    private int id = 12345;

    @Setup(Level.Trial)
    public void setup() {
    }

    // 1. 一回にまとめて + 演算子で結合する
    @Benchmark
    public String stringConcatOnce() {
        int i = id++;

        return names[i % 10] + "," 
                + names[(i + 1) % 10] + "," 
                + names[(i + 2) % 10] + "," 
                + names[(i + 3) % 10] + "," 
                + names[(i + 4) % 10] + "," 
                + names[(i + 5) % 10] + ","
                + names[(i + 6) % 10] + "," 
                + names[(i + 7) % 10] + "," 
                + names[(i + 8) % 10] + "," 
                + names[(i + 9) % 10];
    }

    // 2. 複数回に + 演算子で結合する
    @Benchmark
    public String stringConcatMultiple() {
        int i = id++;

        String name = names[i % 10] + ",";
        name += names[(i + 1) % 10] + ",";
        name += names[(i + 2) % 10] + ",";
        name += names[(i + 3) % 10] + ",";
        name += names[(i + 4) % 10] + ",";
        name += names[(i + 5) % 10] + ",";
        name += names[(i + 6) % 10] + ",";
        name += names[(i + 7) % 10] + ",";
        name += names[(i + 8) % 10] + ",";
        name += names[(i + 9) % 10];

        return name;
    }
}
