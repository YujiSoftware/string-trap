package benchmark;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DivideConcatBenchmark {

    private static final int LENGTH = 10;

    private String[] keys = new String[100];
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private int value = 12345;

    @Setup(Level.Trial)
    public void setup() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < keys.length; i++) {
            for (int j = 0; j < LENGTH; j++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            keys[i] = sb.toString();
            sb.setLength(0);
        }
    }
    
    @Benchmark
    public String concat() {
        String key = keys[value % keys.length];
        int current = value++;
        if (value > 99999) {
            value = 10000;
        }
        String a = keys[current % keys.length];
        String b = keys[(current + 1) % keys.length];

        String text = key + ": " + value + " [a = " + a + ", b = "  + b + "]";

        return text;
    }
    
    @Benchmark
    public String divided() {
        String key = keys[value % keys.length];
        int current = value++;
        if (value > 99999) {
            value = 10000;
        }
        String a = keys[current % keys.length];
        String b = keys[(current + 1) % keys.length];

        String text = key + ": " + value;
        text += "[";
        text += "a = " + a + ",";
        text += "b = " + b;
        text += "]";

        return text;
    }
}
