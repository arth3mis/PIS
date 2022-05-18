package a.vw6;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class Nim implements NimGame {
    private final Random r = new Random();
    public int[] rows;

    private static final Map<Nim, Integer> states = new HashMap<>();

    private boolean log = false;

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
    private Move bestMove;
    public Move bestMove() {
        bestMove = null;
        getWinMove(true);
        if (bestMove == null) bestMove = randomMove();
        return bestMove;
//        return generateSortedMoves()
//                .reduce((x,y) -> play(y).searchWinMove() >= play(x).searchWinMove() ? y : x)
//                .orElse(randomMove());
//        List<Move> l = generateMoves();
//        return l.stream()
//                .reduce(((x, y) -> play(x).miniMax(-1, 1) > play(y).miniMax(-1, 1) ? x : y))
//                .orElse(null);
    }
    public boolean hasBestMove() {
        return searchWinMove() > 0;
    }
    private int getWinMove(boolean setMove) {
        if (isGameOver())
            return -1;
        List<Move> moves = generateMoves();
        for (Move m : moves) {
            if (play(m).getWinMove(false) < 0) {
                if (setMove) bestMove = m;
                return 1;
            }
        }
        return -1;
    }
    private int searchWinMove() {
        if (isGameOver())
            return -1;
        Integer cached = states.get(this);
        if (cached != null)
            return cached;
        Move winMove = generateSortedMoves()
                .filter(m -> play(m).searchWinMove() < 0)  // following state must be a losing state
                .findFirst().orElse(null);
        int rating = winMove == null ? -1 : play(winMove).searchWinMove();
        states.put(this, -1);
        return -1;
    }
    private int miniMax(int player, int depth) {
        Integer cached = states.get(this);
        if (log) System.out.printf("\n%sdepth=%d - hash=%d (%s) - cached=%d - player=%d", "    ".repeat(depth), depth, hashCode(), toString().replace('\n', '.'), cached != null ? cached : 0, player == 1 ? 1 : 2);  // monitoring
        if (cached != null)
            return cached;
        if (isGameOver()) {
            if (log) System.out.print("\t\t\t\t\t\t\t////////////////////////  GAME OVER  ////////////////////////");  // monitoring
            return -player;  // rating function
        }
        List<Move> moves = generateMoves();
        assert !moves.isEmpty();  // testing
        if (log) System.out.printf(" - possible_moves=%d", moves.size());  // monitoring
        IntStream ratings = moves.stream().mapToInt(m -> play(m).miniMax(-player, depth+1));
        int bestRating = player == 1 ? ratings.max().orElse(0) : ratings.min().orElse(0);
        if (log) System.out.printf("\n%sdepth=%d - hash=%d - best_rating=%d", "    ".repeat(depth), depth, hashCode(), bestRating);  // monitoring
        states.put(this, bestRating);
        return bestRating;
    }
    private Stream<Move> generateSortedMoves() {
        return generateMoves().stream().sorted(Comparator.comparingInt(m -> m.number));
    }
    private List<Move> generateMoves() {
        return IntStream.range(0, rows.length).boxed()
                .flatMap(i -> IntStream.rangeClosed(1, rows[i]).mapToObj(n -> Move.of(i, n))).toList();
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
