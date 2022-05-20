package stuff;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ModernJava {

    // repo https://git.thm.de/dhzb87/JbX

    public static void main(String[] args) {
        //streams();
        //int n = Operator.ADD.op.applyAsInt(1, 2);
    }

    static void lambdas() {
        Predicate<Object> l1 = x -> x.hashCode() == 42;

        Function<Object, String> l2 = x -> x.toString()+"";
        Function<Object, String> l2_1 = Objects::toString;

        Consumer<Object> l3 = x -> System.out.println(x+"");
        Consumer<Object> l3_1 = System.out::println;

        Supplier<String> l4 = () -> "42";
        Supplier<Object> l4_1 = Object::new;

        UnaryOperator<Object> l5 = x -> x;

        BinaryOperator<Object> l6 = (x,y) -> y;

        Comparable<Object> l7 = x -> x.hashCode()+1;

        // most stuff has Int_, Long_, Double_ analogs
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
