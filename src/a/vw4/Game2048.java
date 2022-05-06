package a.vw4;

public interface Game2048 {
    /**
     * makes a move based on given instruction
     */
    int play(Move direction);

    /**
     * returns current game state (immutable)
     */
    int[] getGrid();

    /**
     * returns current player points total
     */
    int getScore();

    /**
     * checks for game end
     */
    boolean isGameOver();
}
