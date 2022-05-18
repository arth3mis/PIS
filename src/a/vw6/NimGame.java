package a.vw6;

public interface NimGame {
    NimGame play(Move... moves);
    Move randomMove();
    Move bestMove();
    Move perfectMove();
    boolean isWinning();
    boolean isGameOver();
    String toString();
}
