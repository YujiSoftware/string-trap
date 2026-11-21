package benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * CSV相当の複数行テキストを生成し、文字列結合方法を比較するJMHベンチマーク。
 *
 * エスケープ処理は行わない。全ベンチマークで同じ入力データと出力形式を使用する。
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
public class CsvGenerationBenchmark {

    private static final int COLUMN_COUNT = 8;

    @Param({"100", "1000", "10000", "100000"})
    public int rowCount;

    private String[][] rows;

    @Setup(Level.Trial)
    public void setup() {
        rows = new String[rowCount][COLUMN_COUNT];

        for (int i = 0; i < rowCount; i++) {
            rows[i][0] = Integer.toString(i + 1);
            rows[i][1] = "user" + (i + 1);
            rows[i][2] = "User" + (i + 1);
            rows[i][3] = (i % 3 == 0) ? "Tokyo" : "Saitama";
            rows[i][4] = "Engineer";
            rows[i][5] = "user" + (i + 1) + "@example.com";
            rows[i][6] = (i % 2 == 0) ? "Active" : "Inactive";
            rows[i][7] = "Memo" + (i + 1);
        }
    }

    /**
     * 1行を + で生成し、完成した行を全体用StringBuilderに追加する。
     */
    @Benchmark
    public String plusPerRow() {
        StringBuilder csv = new StringBuilder();

        for (String[] row : rows) {
            String line =
                    row[0] + "," +
                    row[1] + "," +
                    row[2] + "," +
                    row[3] + "," +
                    row[4] + "," +
                    row[5] + "," +
                    row[6] + "," +
                    row[7] + "\r\n";

            csv.append(line);
        }

        return csv.toString();
    }

    /**
     * 1つのStringBuilderに全フィールドを直接追加する。容量はデフォルト。
     */
    @Benchmark
    public String stringBuilderDefaultCapacity() {
        StringBuilder csv = new StringBuilder();

        for (String[] row : rows) {
            appendRow(csv, row);
        }

        return csv.toString();
    }

    /**
     * 1つのStringBuilderに全フィールドを直接追加する。容量を概算で事前確保。
     */
    @Benchmark
    public String stringBuilderPreSized() {
        StringBuilder csv = new StringBuilder(rowCount * 96);

        for (String[] row : rows) {
            appendRow(csv, row);
        }

        return csv.toString();
    }

    /**
     * CSV全体を += で連結する。累積文字列を作り直す比較用ケース。
     */
    @Benchmark
    public String plusWholeDocument() {
        String csv = "";

        for (String[] row : rows) {
            String line =
                    row[0] + "," +
                    row[1] + "," +
                    row[2] + "," +
                    row[3] + "," +
                    row[4] + "," +
                    row[5] + "," +
                    row[6] + "," +
                    row[7] + "\r\n";

            csv += line;
        }

        return csv;
    }

    private static void appendRow(StringBuilder out, String[] row) {
        for (int column = 0; column < COLUMN_COUNT; column++) {
            if (column > 0) {
                out.append(',');
            }
            out.append(row[column]);
        }
        out.append("\r\n");
    }
}
