package a.vw5;

import java.util.*;
import java.util.stream.IntStream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private Random r = new Random();
    public int[] rows;
    private final int bitsPerRow;
    private final int bitsPattern;
    private Map<Integer, Integer> states;  // state, rating
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
        int[] l = Arrays.stream(rows).sorted().toArray();
        int currentState = IntStream.range(0, rows.length)
                .map(i -> l[i] << bitsPerRow * i)
                .reduce((a,b) -> a | b).orElse(0);
        int rating = negamax(1, currentState);
        return null;
    }
    private int negamax(int player, int state) {
        if (state == 0) return -player;
        /*if (states.containsKey(state))
            return states.get(state);*/
        int[] nextStates = calcNextStates(state);
        if (nextStates.length == 0)
            ;
        int worstNextRating = Arrays.stream(nextStates).parallel()
                .map(n -> -negamax(-player, n))
                .max().orElse(0);

        return worstNextRating;
    }
    private int[] calcNextStates(int state) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            int stickCount = (state >> bitsPerRow * i) & bitsPattern;
            int[] temp = {i};
            String trailBits = Integer.toBinaryString(bitsPattern).repeat(temp[0]);
            l.addAll(IntStream.rangeClosed(1, stickCount)
                    .map(n -> {
                        int x = 0;
                        x |= (state & (bitsPattern << bitsPerRow * (rows.length - temp[0] - 1)));  // previous rows
                        x |= ((stickCount - n) << bitsPerRow * temp[0]);  // row i
                        if (!trailBits.isEmpty())
                            x |= (state & Integer.parseInt(trailBits, 2));  // following rows
                        return x;
                    })
                    .boxed().toList());
        }
        return l.stream().mapToInt(Integer::intValue).toArray();
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
