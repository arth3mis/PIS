package a.vw6;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

class Scratch {
    public static void main(String[] args) {
        // example instance for testing & monitoring
        Nim.log = true;
        NimGame g = Nim.of(1,2);
        Move m = g.bestMove();
        System.out.println("\n\n--> Best move: "+m+" to state with hash="+g.play(m).hashCode()+"\n");
        Nim.log = false;

        // reference implementation
        Boolean[][] correctSimulations = new Boolean[100_000][];
        for (int i = 0; i < correctSimulations.length; i++) {
            correctSimulations[i] = simulate(IntStream.generate(() -> new Random().nextInt(1,10)).limit(6).toArray());
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