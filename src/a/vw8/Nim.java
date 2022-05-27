package a.vw8;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private final Random r = new Random();
    public int[] rows;

    private static final Map<Nim, Boolean> states = new HashMap<>();

    public static boolean log = false;
    private static String depth = "";

    public static Nim of(int... rows) {
        return new Nim(rows);
    }

    protected Nim(int... rows) {
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
        for(Move m : moves) nim = nim.play(m);
        return nim;
    }
    public Move randomMove() {
        assert !isGameOver();
        int row;
        do {
            row = r.nextInt(rows.length);
        } while (rows[row] == 0);
        if (rows[row] < 0)
            System.out.println("error");
        int number = r.nextInt(rows[row]) + 1;
        return Move.of(row, number);
    }
    /**
     * My Algorithm
     */
    public Move bestMove() {
        if (log) {
            System.out.printf("\n%shash=%d (%s)", depth, hashCode(), toString().replace('\n', '.'));  // monitoring
            depth += "    ";
        }
        return getWinMove().orElse(randomMove());
    }
    private Optional<Move> getWinMove() {
        return generateMoves()
                .filter(m -> !play(m).findWinMove())
                .reduce((x,y) -> y.number >= x.number ? y : x);
    }
    private boolean findWinMove() {
        if (isGameOver()) {
            if (log) {
                System.out.printf("\n%s[[GAME OVER]]", depth);
            }
            return false;
        }
        Boolean cached = states.get(this);
        if (log) {
            System.out.printf("\n%shash=%d (%s) - cache=%s", depth, hashCode(), toString().replace('\n', '.'), cached==null?"n":cached?"T":"F");  // monitoring
            depth += "    ";
        }
        if (cached != null) {
            if (log) {
                depth = depth.substring(4);
            }
            return cached;
        }
        boolean rating = getWinMove().isPresent();  // indirect recursive call
        if (log) {
            depth = depth.substring(4);
            System.out.printf("\n%shash=%d --> rating=%s", depth, hashCode(), rating?"T":"F");  // monitoring
        }
        states.put(this, rating);
        return rating;
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
        return Arrays.hashCode(Arrays.stream(rows).sorted().toArray());
    }
    /**
     * Nim-Perfect
     */
    public Move perfectMove() {
        assert !isGameOver();
        if (!isWinning()) return randomMove();
        Move m;
        do {
            m = randomMove();
        } while(play(m).isWinning());
        return m;
    }
    public boolean isWinning() {
        return Arrays.stream(rows).reduce(0, (i, j) -> i ^ j) != 0;
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
