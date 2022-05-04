package a.vw4;

import static processing.core.PConstants.*;

public class Game implements Game2048 {

    Grid grid;
    int score;

    // static of() method not possible in PDE/inner class setup
    static Game of(int len) {
        return new Game(len);
    }

    private Game(int len) {
        grid = new Grid(len);
        score = 0;
        grid.random_tile();
        grid.random_tile();
    }

    /**
     * translates arrow key input into grid manipulation
     */
    public int play(int direction) {
        if (isGameOver()) return 0;
        Grid tempGrid = grid.copy();
        int points = 0;
        // player move
        switch (direction) {
            case LEFT:
                points = grid.move();
                break;
            case RIGHT:
                grid.rotate(2);
                points = grid.move();
                grid.rotate(2);
                break;
            case UP:
                grid.rotate();
                points = grid.move();
                grid.rotate(3);
                break;
            case DOWN:
                grid.rotate(3);
                points = grid.move();
                grid.rotate();
            default:
                return 0;
        }
        // add tile if move had an effect
        if (!grid.equals(tempGrid)) {
            grid.random_tile();
        }
        score += points;
        return points;
    }


    public int[] getGrid() {
        return grid.copyData();
    }

    public int getScore() {
        return score;
    }

    /**
     * tries to move in all 4 directions and checks if anything changed.
     * If no move is possible, the game is over.
     * If there are any free slots, the game is not over
     */
    public boolean isGameOver() {
        if (grid.free_slots() > 0)
            return false;
        Grid tempGrid = grid.copy();
        for (int i=1; i<=4; i++) {
            tempGrid.move();
            tempGrid.rotate();
        }
        return tempGrid.equals(grid);
    }

    @Override
    public String toString() {
        return grid.toString();
    }
}
