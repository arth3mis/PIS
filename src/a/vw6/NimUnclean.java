package a.vw6;

import java.util.*;
import java.util.stream.IntStream;

// https://gist.github.com/denkspuren/0abca660e8c483e8b022dad6bdc54109v
//
public class NimUnclean implements NimGame {
    private final Random r = new Random();
    public int[] rows;

    private static final Map<NimUnclean, Integer> states = new HashMap<>();

    private static final Map<NimUnclean, Boolean> stateWin = new HashMap<>();

    public static boolean log = false;

    public static NimUnclean of(int... rows) {
        return new NimUnclean(rows);
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
    protected NimUnclean(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);
    }
    private NimUnclean play(Move m) {
        assert !isGameOver();
        assert m.row < rows.length && m.number <= rows[m.row];
        NimUnclean nim = NimUnclean.of(rows);
        nim.rows[m.row] -= m.number;
        return nim;
    }
    public NimUnclean play(Move... moves) {
        NimUnclean nim = this;
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
//        return getWinMove().orElse(randomMove());
//        return generateSortedMoves()
//                .reduce((x,y) -> play(y).searchWinMove() >= play(x).searchWinMove() ? y : x)
//                .orElse(randomMove());
        return generateMoves().stream()
                .reduce(((x, y) -> play(x).negaMax(-1, 1) <= play(y).negaMax(-1, 1) ? x : y))
                .orElse(randomMove());
    }
    private List<Move> generateMoves() {
        return IntStream.range(0, rows.length).boxed()
                .flatMap(i -> IntStream.rangeClosed(1, rows[i]).mapToObj(n -> Move.of(i, n))).toList();
    }
    private Optional<Move> getWinMove() {
        return generateMoves().stream()
                .filter(m -> !play(m).findWinMove())
                .reduce((x,y) -> x.number > y.number ? x : y);
    }
    private boolean findWinMove() {
        if (isGameOver())
            return false;
        Boolean cached = stateWin.get(this);
        if (cached != null) {
            return cached;
        }
        boolean rating = getWinMove().isPresent();
        stateWin.put(this, rating);
        return rating;
    }
    private int searchWinMove() {
        if (isGameOver())
            return -1;
        Integer cached = states.get(this);
        if (cached != null) {
            return cached;
        }
        Move winMove = generateMoves().stream()
                .filter(m -> play(m).searchWinMove() < 0)  // following state must be a losing state
                .findFirst().orElse(null);
        int rating = winMove == null ? -1 : play(winMove).searchWinMove();
        states.put(this, -1);
        return -1;
    }
    private int negaMax(int player, int depth) {
        Integer cached = states.get(this);
        if (log) System.out.printf("\n%sdepth=%d - hash=%d (%s) - cached=%d - player=%d", "    ".repeat(depth), depth, hashCode(), toString().replace('\n', '.'), cached != null ? cached : 0, player == 1 ? 1 : 2);  // monitoring
        if (cached != null)
            return cached;
        if (isGameOver()) {
            if (log) System.out.print("\t\t\t\t\t\t\t////////////////////////  GAME OVER  ////////////////////////");  // monitoring
            return -1;  // rating function
        }
        List<Move> moves = generateMoves();
        assert !moves.isEmpty();  // testing
        if (log) System.out.printf(" - possible_moves=%d", moves.size());  // monitoring
        IntStream ratings = moves.stream().mapToInt(m -> -play(m).negaMax(-player, depth+1));
//        int bestRating = player == 1 ? ratings.max().orElse(0) : ratings.min().orElse(0);
        int bestRating = ratings.max().getAsInt();
        if (log) System.out.printf("\n%sdepth=%d - hash=%d - best_rating=%d", "    ".repeat(depth), depth, hashCode(), bestRating);  // monitoring
        states.put(this, bestRating);
        return bestRating;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Arrays.equals(Arrays.stream(rows).sorted().toArray(), Arrays.stream(((NimUnclean)obj).rows).sorted().toArray());
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
