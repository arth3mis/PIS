package a.vw5;

import java.util.Arrays;

public interface NimGame {
    NimGame play(Move... moves);
    int[] getBoard();
    Move findBestMove();
    Move randomMove();
    boolean isGameOver();
    String toString();
}
