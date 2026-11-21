
package benchmark;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class StringConcatBenchmark {

    private static final String PATTERN = "%s: %d";

    private final String[] names = {"foo", "bar", "baz"};
    private int id = 12345;

    @Setup(Level.Trial)
    public void setup() {
    }

    // 1. + 演算子
    @Benchmark
    public String stringConcatOperator() {
        int current = id++;
        if (id > 99999) {
            id = 10000;
        }
        String name = names[current % names.length];

        return name + ": " + current;
    }

    // 2. StringBuilder
    @Benchmark
    public String stringBuilder() {
        int current = id++;
        if (id > 99999) {
            id = 10000;
        }
        String name = names[current % names.length];

        return new StringBuilder()
            .append(name)
            .append(": ")
            .append(current)
            .toString();
    }

    // 3. String.format()
    @Benchmark
    public String stringFormat() {
        int current = id++;
        if (id > 99999) {
            id = 10000;
        }
        String name = names[current % names.length];

        return String.format(Locale.ROOT, PATTERN, name, current);
    }

    // 4. String.concat()
    @Benchmark
    public String stringConcat() {
        int current = id++;
        if (id > 99999) {
            id = 10000;
        }
        String name = names[current % names.length];

        return name.concat(": ").concat(String.valueOf(current));
    }
}
