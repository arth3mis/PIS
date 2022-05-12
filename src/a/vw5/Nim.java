package a.vw5;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private Random r = new Random();
    public int[] rows;
    private final int bitsPerRow;
    private final int bitsPattern;
    private static Map<Integer, Double> states;  // state, rating
    public static Nim of(int... rows) {
        return new Nim(rows);
    }
    private Nim(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);
        bitsPerRow = (int) Math.ceil(Math.log(Arrays.stream(rows).max().orElse(1) + 1) / Math.log(2));
        bitsPattern = Integer.parseInt("1".repeat(bitsPerRow), 2);
        if (bitsPerRow * rows.length <= 32)
            states = new HashMap<>();
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
        for(Move m : moves) nim = nim.play(m);
        return nim;
    }
    public int[] getBoard() {
        return Arrays.copyOf(rows, rows.length);
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
    public Move findBestMove() {
        if (states == null) return null;
        int currentState = stateHash(rows);
        int nextState = Arrays.stream(calcNextStates(currentState))
                .reduce((a, b) -> minimax(1, 1, a) >= minimax(1, 1, b) ? a : b).orElse(-1);
        // decode move
        int[] diff = stateUnhash(currentState - nextState);
        for (int i = 0; i < diff.length; i++) {
            if (diff[i] > 0) {
                return Move.of(i, diff[i]);
            }
        }
        return null;
    }
    private double minimax(int player, int depth, int state) {
        if (state == 0) return player / (double) depth;  // rating function
        if (states.containsKey(state))
            return states.get(state);
        DoubleStream ds = Arrays.stream(calcNextStates(state))//.parallel()
                .mapToDouble(s -> minimax(-player, depth+1, s));
        double rating = player == 1 ? ds.max().orElse(0) : ds.min().orElse(0);
        states.put(state, rating);
        System.out.println(Arrays.toString(stateUnhash(state)) + " = " + rating);  // debug
        return rating;
    }
    private int[] calcNextStates(int state) {
        List<Integer> l = new ArrayList<>();
        int[] r = stateUnhash(state);
        for (int i = 0; i < r.length; i++) {
            //l.addAll(IntStream.rangeClosed(1, r[i]).map(n -> n).boxed().toList());
            for (int j = 1; j <= r[i]; j++) {
                r[i] -= j;
                l.add(stateHash(r));
                r[i] += j;
            }
        }
        return l.stream().distinct().mapToInt(i -> i).toArray();
    }
    private int stateHash(int[] r) {
        int[] l = Arrays.stream(r).sorted().toArray();
        return IntStream.range(0, r.length)
                .map(i -> l[i] << bitsPerRow * i)
                .reduce((a,b) -> a | b).orElse(0);
    }
    private int[] stateUnhash(int state) {
        return IntStream.range(0, rows.length).map(i -> (state >> bitsPerRow * i) & bitsPattern).toArray();
    }
    public boolean isGameOver() {
        return Arrays.stream(rows).allMatch(n -> n == 0);
    }
    public String toString() {
        String s = "";
        for(int n : rows) {
            s += "\n" + "I ".repeat(n);
            if (n == 0) s+= "*";
        }
        return s + "\n";
    }
}
