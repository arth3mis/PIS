package a.vw6;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

class Scratch {
    public static void main(String[] args) {
        // example instance for testing & monitoring
        NimGame g = Nim.of(1,2);
        System.out.println("\n"+g.bestMove());

        // reference implementation
        Boolean[][] correctSimulations = new Boolean[0][];
        for (int i = 0; i < correctSimulations.length; i++) {
            correctSimulations[i] = simulate(IntStream.generate(() -> new Random().nextInt(1,10)).limit(6).toArray());
        }
        System.out.printf("\nplayed as predicted (out of %d games): %d\npredicted losses: %d",
                correctSimulations.length, Arrays.stream(correctSimulations).filter(b -> b[0]).count(),
                Arrays.stream(correctSimulations).filter(b -> !b[1]).count());
    }

    static Boolean[] simulate(int[] rows) {
        NimGame g = Nim.of(rows);
        boolean beginnerIsWinner = g.isWinning();
        // minimax starts and should achieve the predicted result
        boolean perfectTurn = true;  // is set to false in loop, minimax algorithm makes first move
        while (!g.isGameOver()) {
            perfectTurn = !perfectTurn;
            g = g.play(perfectTurn ? g.perfectMove() : g.bestMove());
        }
        return new Boolean[]{perfectTurn ^ beginnerIsWinner, beginnerIsWinner};
    }
}