package stuff;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ModernJava {

    // repo https://git.thm.de/dhzb87/JbX

    public static void main(String[] args) {
        //streams();
        //int n = Operator.ADD.op.applyAsInt(1, 2);
    }

    enum Operator {
        ADD((x,y) -> x + y),
        SUB((x,y) -> x - y),
        MUL((x,y) -> x * y),
        DIV((x,y) -> x / y),
        MOD((x,y) -> x % y);
        final IntBinaryOperator op;
        Operator(IntBinaryOperator o) { op = o; }
    }

    static void streams() {
        int a = IntStream.range(1, 101).sum();
        long b = LongStream.rangeClosed(1, 1_000_000_000).sum();
        long c = LongStream.rangeClosed(1, 1_000_000_000).parallel().sum();  // nebenläufig, nutzt alle Kerne
        int[] d = {1,5,10,20};
        try {
            double e = Arrays.stream(d).parallel().average().orElseThrow();
        } catch (NoSuchElementException ignored) {}
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof ModernJava that)) return false;
        return that.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(42);
    }
}
