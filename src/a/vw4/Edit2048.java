package a.vw4;

import processing.core.PApplet;

public class Edit2048 extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Edit2048());
    }

    Game2048 game2048;
    boolean game;

    @Override
    public void settings() {
        size(600, 600);
    }

    @Override
    public void setup() {
        textAlign(CENTER, CENTER);
        textSize(27);
        noStroke();
        background(color(179, 189, 214));
        colorMode(HSB, 360, 100, 100);

        resetGame();
        show(game2048.getGrid());
    }

    void resetGame() {
        game2048 = Game.of(4);
        game = true;
    }

    /**
     * graphical game display
     */
    void show(final int[] grid) {
        int L = (int)sqrt(grid.length);
        float margin = 0.05f * width;
        float space = (width-margin*2) / L;
        float SIZE_TILE = space * 0.8f;
        float SIZE_BORDER = space * 0.2f;
        int i = 0;
        float X, Y;
        for (int y=0; y<L; y++) {
            Y = margin+SIZE_BORDER/2+y*(SIZE_TILE+SIZE_BORDER);
            for (int x=0; x<L; x++) {
                X = margin+SIZE_BORDER/2+x*(SIZE_TILE+SIZE_BORDER);
                // fill(color(179, 189, 214));
                fill(color(30+log(grid[i]+1)/log(2)*10, 100, 100));
                rect(X, Y, SIZE_TILE, SIZE_TILE, 15);
                if (grid[i] != 0) {
                    fill(color(271, 0, 1));
                    text(grid[i], X+SIZE_TILE/2+1, Y+SIZE_TILE/2+1);
                }
                i++;
            }
        }
    }

    @Override
    public void keyPressed() {
        // arrow key?
        if (key == CODED && game) {
            game2048.play(keyCode - LEFT);
            show(game2048.getGrid());
        }
        // reset game?
        else if (key == BACKSPACE) {
            resetGame();
            show(game2048.getGrid());
            println("RESET");
        }
        // print score?
        else if (key == 's') {
            println(game2048.getScore());
        }
        // game over?
        if (game2048.isGameOver()) {
            game = false;
            println("GAME OVER. YOUR SCORE =",game2048.getScore());
        }
    }

    @Override
    public void draw() {
    }
}
