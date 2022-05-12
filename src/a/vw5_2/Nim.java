package a.vw5_2;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private final Random r = new Random();
    public int[] rows;

    public static Map<Nim, Integer> states = new HashMap<>();
    private final int bitsPerRow;

    public static Nim of(int... rows) {
        return new Nim(rows);
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
    private Nim(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);

        bitsPerRow = (int) Math.ceil(Math.log(Arrays.stream(rows).max().orElse(1) + 1) / Math.log(2));
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
        return generateMoves()
                .reduce(((x, y) -> play(x).miniMax(1, 1) > play(y).miniMax(1, 1) ? x : y))
                .orElse(null);
    }
    private int miniMax(int player, int depth) {
        Integer cached = states.get(this);
        if (cached != null)
            return cached;
        if (isGameOver())
            return -player;  // rating
        Stream<Move> moves = generateMoves();
        IntStream ratings = moves.mapToInt(m -> play(m).miniMax(-player, depth+1));
        int bestRating = player == 1 ? ratings.max().orElse(0) : ratings.min().orElse(0);
        states.put(this, bestRating);
        return bestRating;
    }
    private Stream<Move> generateMoves() {
        return IntStream.range(0, rows.length).boxed()
                .flatMap(i -> IntStream.rangeClosed(1, rows[i]).mapToObj(n -> Move.of(i, n)));
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Arrays.equals(Arrays.stream(rows).sorted().toArray(), Arrays.stream(((Nim)obj).rows).sorted().toArray());
    }
    @Override
    public int hashCode() {
        int[] l = Arrays.stream(rows).sorted().toArray();
        return IntStream.range(0, rows.length)
                .map(i -> l[i] << bitsPerRow * i)
                .reduce((a,b) -> a | b).orElse(0);
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
