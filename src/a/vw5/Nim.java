package a.vw5;

import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private Random r = new Random();
    public int[] rows;
    private final int bitsPerRow;
    private final int bitsPattern;
    private Map<State, Double> states;
    public static Nim of(int... rows) {
        return new Nim(rows);
    }
    private Nim(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);
        bitsPerRow = (int) Math.ceil(Math.log(Arrays.stream(rows).max().orElse(1)) / Math.log(2));
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
        State currentState = new State(rows, bitsPerRow);
        double rating = minimax(1, 1, currentState);

        System.out.println("\n" + rating);

        State s = new State(new int[]{1}, 3);
        states.put(s, 42.0);
        System.out.println(states.containsKey(s));
        System.out.println(states.containsKey(new State(new int[]{1}, 3)));
        return null;
    }
    private double minimax(int player, int depth, State state) {
        if (state.get() == 0) return -player / (double) depth;  // rating function
        if (states.containsKey(state))
            return states.get(state);
        DoubleStream ds = calcNextStates(state).parallel()
                .mapToDouble(s -> minimax(-player, depth+1, s));
        double rating = player == 1 ? ds.max().orElse(0) : ds.min().orElse(0);
        states.put(state, rating);
        System.out.println(Arrays.toString(stateUnhash(state.get())) + " = " + rating);
        return rating;
    }
    private Stream<State> calcNextStates(State state) {
        List<State> l = new ArrayList<>();
        int[] r = stateUnhash(state.get());
        for (int i = 0; i < r.length; i++) {
            for (int j = 1; j <= r[i]; j++) {
                r[i] -= j;
                l.add(new State(Arrays.stream(r).sorted().toArray(), bitsPerRow));
                r[i] += j;
            }
        }
        return l.stream().distinct();
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
