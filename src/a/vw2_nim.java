package a;

import herzi.nim.Nim;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class vw2_nim extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new vw2_nim());
    }

    @Override
    public void settings() {
        //fullScreen();
        size(600, 700);
        smooth(8);
    }

    // variables
    //
    Nim game;
    Field field;
    float pixelSize = 10;

    int colourSelected = color(139, 0, 0);
    int colourHover = color(255, 99, 71);

    @Override
    public void setup() {
        frameRate(60);
        resetGame();
        // setup arrow keys and space for trigger
        keyTriggered.put(UP, false);
    }

    void resetGame() {
        //game = Nim.of(Nim.randomSetup(5,5,5));
        //field = new Field(game.rows);
        field = new Field(new int[]{1});
    }

    @Override
    public void draw() {
        background(222, 184, 135);
        field.draw();
    }


    class Field {
        Stick[][] sticks;
        PVector stickHovered;  // changed by mouse/keys
        // display
        float stickSizeX;  // % of y

        Field(int[] rows) {
            stickSizeX = 0.35f;
            sticks = new Stick[rows.length][];
            for (int i = 0; i < rows.length; i++) {
                sticks[i] = new Stick[rows[i]];
                for (int j = 0; j < sticks[i].length; j++) {
                    sticks[i][j] = new Stick(0,0,0,0,10);
                }
            }
        }

        void setHovered(float mouseX, float mouseY) {
            stickHovered = null;
        }

        void draw() {
            for (Stick[] stick : sticks) {
                for (Stick s : stick) {
                    s.draw();
                }
            }
        }
    }


    class Stick {
        PVector pos;
        PVector size;  // includes border
        float borderPercent;  // border size relative to size.x
        int colour = color(205, 133, 63);
        int borderColour = color(139, 69, 19);
        boolean selected;
        boolean removed;

        /**
         * x,y = pos;
         * sx, sy = size
         */
        Stick(float x, float y, float sx, float sy, float borderPercent) {
            pos = new PVector(x, y);
            size = new PVector(sx, sy);
            this.borderPercent = borderPercent;
        }

        void draw() {
            noStroke();
            fill(borderColour);
            rect(pos.x, pos.y, size.x, size.y);
            fill(colour);
            rect(pos.x + size.x*borderPercent, pos.y + size.x*borderPercent,
                    size.x - 2*size.x*borderPercent, size.y - 2*size.x*borderPercent);
        }
    }

    @Override
    public void mousePressed() {

    }

    @Override
    public void mouseReleased() {

    }

    float mouseWheelDelta = 0;

    @Override
    public void mouseWheel(MouseEvent event) {
        mouseWheelDelta += event.getCount();
    }

    boolean[] keyPressed = new boolean[1000];
    Map<Integer, Boolean> keyTriggered = new HashMap<>();

    @Override
    public void keyPressed() {
        if (key == CODED) {
            keyPressed[keyCode] = true;
            keyTriggered.replace(keyCode, true);
        }
    }

    @Override
    public void keyReleased() {
        if (key == CODED) {
            keyPressed[keyCode] = false;
        }
    }
}
