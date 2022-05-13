package j.vw4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Scratch {
    public static void main(String[] args) {
        int[] rowArray = new int[]{9, 9, 9, 9, 9, 9, 9, 9, 9};
        NimGame nim = new BigNimKi(rowArray);

        System.out.println("Best move:");
        System.out.println(nim.bestMove());
    }
}

class BigNimKi implements NimGame {

    static long timeSum = 0;
    static int cacheSum = 0;
    static int recursiveCalls = 0;
    static long timeSumTotal = 0;
    static int cacheSumTotal = 0;
    static int recursiveCallsTotal = 0;
    static int calculatedMovesTotal = 0;

    static HashMap<NimGame, Double> calculatedNimGames = new HashMap<>();

    private Random r = new Random();
    int[] rows;

    public BigNimKi(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);
    }


    @Override
    public Move bestMove() {
        if (isGameOver()) return null;

        long startTime = System.currentTimeMillis();
        Move bestMove = allMoves(this).reduce((m1, m2) ->
                computeScore(play(m1), false) < computeScore(play(m2), false) ? m1 : m2).get();

        // Print some Data
        {
            timeSum = System.currentTimeMillis() - startTime;
            timeSumTotal += timeSum;
            cacheSumTotal += cacheSum;
            recursiveCallsTotal += recursiveCalls;
            calculatedMovesTotal++;

            if (recursiveCalls != 0)
                System.out.println("Turn Time: " + (timeSum) + "ms | Cache percentage: " + (cacheSum / (double) recursiveCalls) + " | Number of Recursive Calls: " + recursiveCalls);
            else
                System.out.println("No recursive calls needed");
            if (recursiveCallsTotal != 0)
                System.out.println("Average Time: " + (timeSumTotal / calculatedMovesTotal) + "ms | Average percentage: " + (cacheSumTotal / (double) recursiveCallsTotal) + " | Sum of Recursive Calls: " + recursiveCallsTotal);

            timeSum = 0;
            cacheSum = 0;
            recursiveCalls = 0;
        }
        return bestMove;
    }


    private double computeScore(NimGame nim, boolean isMyTurn) {
        // Abbruchbedingung
        if (nim.isGameOver()) return -1;

        recursiveCalls++;

        // Look if NimGame is already in HashMap
        Double cachedItem = calculatedNimGames.get(nim);
        if (cachedItem != null) {
            cacheSum++;
            return cachedItem;
        }

        // Get scores of all following moves
        DoubleStream subScores = allMoves(nim).mapToDouble(move -> computeScore(nim.play(move), !isMyTurn));

        // Return best score
        double score = -subScores.min().getAsDouble();
        calculatedNimGames.put(nim, score);
        return score;
    }


    // Returns all possible moves from given NimGame as a stream
    private Stream<Move> allMoves(NimGame nim) {
        rows = ((BigNimKi) nim).rows;
        return IntStream.range(0, rows.length).boxed()
                .flatMap(i -> IntStream.rangeClosed(1, rows[i]).mapToObj(j -> new Move(i, j)));
    }


    // Hash code for sorted Array
    @Override
    public int hashCode() {
        return Arrays.hashCode(Arrays.stream(rows).sorted().toArray());
        //return Arrays.hashCode(rows);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NimGame)) return false;
        return Arrays.equals(Arrays.stream(rows).sorted().toArray(),
                Arrays.stream(((BigNimKi) obj).rows).sorted().toArray());
        //return Arrays.equals(rows, ((BigNimKi) obj).row);
    }


    // Just the play-method returning an instance if BigNimKi instead of Nim
    private NimGame play(Move m) {
        assert !isGameOver();
        assert m.row < rows.length && m.number <= rows[m.row];
        BigNimKi nim = new BigNimKi(rows);
        nim.rows[m.row] -= m.number;
        return nim;
    }

    // Just the play-method returning an instance if BigNimKi instead of Nim
    @Override
    public NimGame play(Move... moves) {
        NimGame nim = this;
        for(Move m : moves) nim = this.play(m);
        return nim;
    }

    @Override
    public Move randomMove() {
        assert !isGameOver();
        int row;
        do {
            row = r.nextInt(rows.length);
        } while (rows[row] == 0);
        int number = r.nextInt(rows[row]) + 1;
        return Move.of(row, number);
    }

    @Override
    public boolean isGameOver() {
        return Arrays.stream(rows).allMatch(n -> n == 0);
    }


}

class Move {
    public final int row, number;

    public static Move of(int row, int number) {
        return new Move(row, number);
    }

    public Move(int row, int number) {
        if (row < 0 || number < 1) throw new IllegalArgumentException();
        this.row = row;
        this.number = number;
    }

    public String toString() {
        return "(" + row + ", " + number + ")";
    }
}

interface NimGame {
    static boolean isWinning(int... numbers) {
        return Arrays.stream(numbers).reduce(0, (i, j) -> i ^ j) != 0;
        // klassische Variante:
        // int res = 0;
        // for(int number : numbers) res ^= number;
        // return res != 0;
    }
    NimGame play(Move... moves);
    Move randomMove();
    Move bestMove();
    boolean isGameOver();
    String toString();
}