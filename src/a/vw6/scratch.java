package a.vw6;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

class TestNim {
    public static void main(String[] args) {

        /*
        MISSING: test on micro level (method testing)
         */

        // example instance for testing & monitoring
        Nim.log = true;
        NimGame g = Nim.of(1,2);
        Move m = g.bestMove();
        if (Nim.log) System.out.printf("\n\n--> Best move: %s to state with hash=%d\n", m, g.play(m).hashCode());
        assert m.equals(Move.of(1,1));
        Nim.log = false;

        // more test cases
        assert Nim.of(3,3,3).bestMove().equals(Move.of(2,3));
        assert Nim.of(8,6,4).bestMove().equals(Move.of(0,6));
        assert Nim.of(5,6,1,8).bestMove().equals(Move.of(3,6));

        // reference implementation
        Boolean[][] correctSimulations = new Boolean[100_000][];
        System.out.println("\nProgress:");
        for (int i = 0; i < correctSimulations.length; i++) {
            correctSimulations[i] = simulate(IntStream.generate(() -> new Random().nextInt(1,4)).limit(4).toArray());
            if (i%(correctSimulations.length/100) == 0) System.out.print("o");
        }
        System.out.printf("\n\nplayed as predicted (out of %,d games): %,d\npredicted losses: %,d",
                correctSimulations.length, Arrays.stream(correctSimulations).filter(b -> b[0]).count(),
                Arrays.stream(correctSimulations).filter(b -> !b[1]).count());
    }

    static Boolean[] simulate(int[] rows) {
        NimGame g = Nim.of(rows);
        boolean winsAgainstPerfect = g.isWinning();
        //System.out.printf("%s - will win: %5b", g.toString().replace('\n', '.'), winsAgainstPerfect);
        // minimax starts and should achieve the predicted result
        boolean perfectTurn = false;
        while (!g.isGameOver()) {
            g = g.play(perfectTurn ? g.perfectMove() : g.bestMove());
            perfectTurn = !perfectTurn;
        }
        //System.out.printf(" - correct guess: %5b\n", perfectTurn == winsAgainstPerfect);
        return new Boolean[]{perfectTurn == winsAgainstPerfect, winsAgainstPerfect};
    }
}