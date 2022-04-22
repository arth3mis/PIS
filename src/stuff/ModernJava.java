package stuff;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ModernJava {

    // repo https://git.thm.de/dhzb87/JbX

    public static void main(String[] args) {
        //streams();
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
}
