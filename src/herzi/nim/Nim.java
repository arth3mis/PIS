package herzi.nim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private Random r = new Random();
    public int[] rows;

    public static Nim of(int... rows) {
        return new Nim(rows);
    }

    public static int[] randomSetup(int... maxN) {
        Random r = new Random();
        int[] rows = new int[maxN.length];
        for(int i = 0; i < maxN.length; i++) {
            rows[i] = r.nextInt(maxN[i]) + 1;
        }
        return rows;
    }

    public static ArrayList<Move> autoplay(NimGame nim) {
        ArrayList<Move> moves = new ArrayList<>();
        while (!nim.isGameOver()) {
            Move m = nim.bestMove();
            moves.add(m);
            nim = nim.play(m);
        }
        return moves;
    }

    public static boolean simulateGame(int... maxN) {
        Nim nim = Nim.of(randomSetup(maxN));
        // System.out.println(nim);
        // System.out.println((NimGame.isWinning(nim.rows) ? "first" : "second") + " to win");
        ArrayList<Move> moves = autoplay(nim);
        // System.out.println(moves);
        return (NimGame.isWinning(nim.rows) && (moves.size() % 2) == 1) ||
                (!NimGame.isWinning(nim.rows) && (moves.size() % 2) == 0);
    }
    
    private Nim(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);
    }
    private Nim play(Move m) {
        assert !isGameOver();
        assert m.row < rows.length && m.number <= rows[m.row];
        Nim nim = Nim.of(rows);
        nim.rows[m.row] -= m.number;
        return nim;
    }
    public Nim play(Move... moves) {
        Nim nim = this;
        for(Move m : moves) nim = play(m);
        return nim;
    }
    public Move randomMove() {
        assert !isGameOver();
        int row;
        do {
            row = r.nextInt(rows.length);
        } while (rows[row] == 0);
        int number = r.nextInt(rows[row]) + 1;
        return Move.of(row, number);
    }
    public Move bestMove() {
        assert !isGameOver();
        if (!NimGame.isWinning(rows)) return randomMove();
        Move m;
        do {
            m = randomMove();
        } while(NimGame.isWinning(play(m).rows));
        return m;
    }
    public boolean isGameOver() {
        return Arrays.stream(rows).allMatch(n -> n == 0);
    }
    public String toString() {
        String s = "";
        for(int n : rows) s += "\n" + "I ".repeat(n);
        return s;
    }
}
