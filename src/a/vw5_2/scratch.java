package a.vw5_2;

import a.vw6.NimUnclean;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

class Scratch {
    public static void main(String[] args) {
        NimGame g;
        g = Nim.of(IntStream.range(0, 6).map(i -> i > 0 ? new Random().nextInt(10) : 9).toArray());
        g = Nim.of(2,2);
        g = Nim.of(9,9,9,9,9,9);
        g = Nim.of(4,4,4,4);
//        System.out.println(g);
//        System.out.println(g.bestMove());
        simulate(100_000);
    }

    static void simulateWins(int n) {
        Boolean[] wins = new Boolean[n];
        System.out.println("\n" + "_".repeat(46) + "progress" + "_".repeat(45));
        for (int i = 0; i < n; i++) {
            Nim g = Nim.of(IntStream.generate(() -> new Random().nextInt(1,31)).limit(30).toArray());
            wins[i] = herzi.nim.NimGame.isWinning(g.rows);
            if (i > 20 && i % (n/100) == 0)
                System.out.print("o");
            //Nim.states.clear();
        }
        System.out.printf("\n\nwins in %,d random games: %,d", n, Arrays.stream(wins).filter(b -> b).count());
    }

    static void simulate(int n) {
        long[] times = new long[n];
        for (int i = 0; i < n; i++) {
            NimUnclean g = NimUnclean.of(IntStream.range(0, 6).map(x -> new Random().nextInt(1,10)).toArray());
            long start = System.nanoTime();
            g.bestMove();
            times[i] = System.nanoTime() - start;
            if (i < 20)
                System.out.println(times[i] + " (" + Arrays.stream(g.rows).sum() + " sticks)");
            if (i == 20)
                System.out.println("\n" + "_".repeat(46) + "progress" + "_".repeat(45));
            if (i > 20 && i % (n/100) == 0)
                System.out.print("o");
            //Nim.states.clear();
        }
        System.out.printf("\n\naverage over %,d games: %.2fns", n, Arrays.stream(times).average().orElse(0));
    }
}