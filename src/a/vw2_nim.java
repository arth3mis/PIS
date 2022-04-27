package a;

import herzi.nim.Move;
import herzi.nim.Nim;
import herzi.nim.NimGame;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.*;

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
    int[] gameRows;
    NimGame game;
    Field field;

    int colourHover = color(250, 235, 215);
    int colourHoverIllegal = color(100, 110, 120);
    int colourHoverSelected = color(255, 99, 71);
    int colourSelected = color(139, 0, 0);

    @Override
    public void setup() {
        frameRate(60);
        resetGame();
        // setup trigger keys
        keyTriggered.put(UP, false);
        keyTriggered.put(DOWN, false);
        keyTriggered.put(LEFT, false);
        keyTriggered.put(RIGHT, false);
        keyTriggered.put((int)' ', false);
        keyTriggered.put((int)ENTER, false);
        keyTriggered.put((int)BACKSPACE, false);
    }

    void resetGame() {
        gameRows = Nim.randomSetup(5,5,5,5);
        game = Nim.of(gameRows);
        field = new Field(gameRows);
    }

    @Override
    public void draw() {
        update();

        background(222, 184, 135);
        field.draw();
    }

    void update() {
        field.update(mouseX, mouseY);

        if (keyTriggered.get((int)BACKSPACE)) {
            keyTriggered.replace((int)BACKSPACE, false);
            resetGame();
        }
    }


    class Field {
        Stick[] sticks;
        Stick stickHovered;  // changed by mouse/keys
        int selectRow;
        boolean mouseHover;
        Button btnRemove;

        Field(int[] rows) {
            // display values
            float yPadding = 0.08f;
            float yHeight = 1 - 2.2f * yPadding;
            float yStickSpace = yHeight / rows.length;
            float yStickSize = yStickSpace * 0.85f;
            float xStickSize = yStickSize * 0.35f;
            float xStickSpace = xStickSize * 1.8f;

            // check width
            for (int row : rows) {
                while (xStickSpace > xStickSize * 1.01f &&
                        (xStickSpace * row - (xStickSpace - xStickSize)) * width > width * 0.95) {  // check with formula below
                    xStickSpace -= xStickSize * 0.01f;
                }
            }

            // init sticks
            int n = 0;
            sticks = new Stick[Arrays.stream(rows).sum()];
            for (int i = 0; i < rows.length; i++) {
                float xWidth = xStickSpace * rows[i] - (xStickSpace - xStickSize);  // check with formula above
                for (int j = 0; j < rows[i]; j++) {
                    sticks[n++] = new Stick(
                            0.5f - xWidth / 2 + xStickSpace * j, yPadding + yStickSpace * i,
                            xStickSize, yStickSize,
                            0.15f, i);
                }
            }

            selectRow = -1;
            btnRemove = new Button(-0.04f, -0.02f, -0.2f, -0.05f);
        }

        void toggleSelectHovered() {
            if (stickHovered != null && (stickHovered.row == selectRow || selectRow == -1)) {
                stickHovered.selected = !stickHovered.selected;
                selectRow = stickHovered.row;
            }
        }

        void removeSelected() {
            game.play(Move.of(selectRow, (int) Arrays.stream(sticks).filter(s -> s.selected).count()));
            Arrays.stream(sticks).filter(s -> s.selected).forEach(s -> {
                s.selected = false;
                s.removed = true;
            });
            // computer move
            Move m = game.bestMove();
            game.play(m);
            List<Stick> ls = new ArrayList<>(Arrays.stream(sticks).filter(s -> s.row == m.row && !s.removed).toList());
            Collections.shuffle(ls);
            ls.stream().limit(m.number).forEach(s -> {
                s.selected = false;
                s.removed = true;
            });
        }

        void onMouseClick() {
            if (btnRemove.hovered)
                removeSelected();
            else if (mouseHover)
                toggleSelectHovered();
        }

        void update(float mouseX, float mouseY) {
            // hover
            mouseHover = false;
            for (Stick s : sticks) {
                s.update();
                if (s.hover(mouseX, mouseY)) {
                    stickHovered = s;
                    mouseHover = true;
                }
            }
            btnRemove.hover(mouseX, mouseY);
            // reset select row
            if (Arrays.stream(sticks).noneMatch(s -> s.selected))
                selectRow = -1;
        }

        void draw() {
            for (Stick s : sticks) {
                s.draw(selectRow);
            }
            btnRemove.draw();
        }
    }


    class Stick {
        int row;
        PVector pos;
        PVector size;  // includes border
        float borderPercent;  // border size relative to size.x
        int colour = color(205, 133, 63);
        int borderColour = color(150, 82, 45);//color(139, 69, 19);
        boolean hovered;
        boolean selected;
        boolean removed;
        float removeAnimation;

        Stick(float x, float y, float sx, float sy, float borderPercent, int row) {
            pos = new PVector(x, y);
            size = new PVector(sx, sy);
            this.borderPercent = borderPercent;
            this.row = row;
            removeAnimation = 1;
        }

        boolean hover(float mouseX, float mouseY) {
            if (removed) return false;
            return hovered =
                    mouseX >= pos.x * width && mouseX < (pos.x+size.x) * width &&
                    mouseY >= pos.y * height && mouseY < (pos.y+size.y) * height;
        }

        void update() {
            if (removed && removeAnimation > 0) {
                removeAnimation = max(removeAnimation - 0.03f, 0);
            }
        }

        void draw(int selectRow) {
            noStroke();
            fill(hovered ? (selected ? colourHoverSelected : colourHover) : (selected ? colourSelected : borderColour), removeAnimation*255);
            if (hovered && selectRow != -1 && selectRow != row)
                fill(colourHoverIllegal);
            rect(pos.x * width, pos.y * height, size.x * width, size.y * height);
            fill(colour, removeAnimation*255);
            rect((pos.x + size.x*borderPercent) * width, (pos.y + size.x*borderPercent) * height,
                    (size.x - 2*size.x*borderPercent) * width, (size.y - 2*size.x*borderPercent) * height);
        }
    }

    class Button {
        PVector pos;
        PVector size;
        PFont fontBtn;
        boolean hovered;

        /**negative values are relative to right/bottom*/
        Button(float x, float y, float sx, float sy) {
            if (x < 0) x = 1 + x + (sx < 0 ? sx : 0);
            if (y < 0) y = 1 + y + (sy < 0 ? sy : 0);
            pos = new PVector(x, y);
            size = new PVector(abs(sx), abs(sy));
            fontBtn = createFont("Arial black", size.y * height * 0.7f);
        }

        boolean hover(float mouseX, float mouseY) {
            return hovered = mouseX >= pos.x * width && mouseX < (pos.x + size.x) * width && mouseY >= pos.y * height && mouseY < (pos.y + size.y) * height;
        }

        void draw() {
            stroke(136, 70, 36);
            strokeWeight(4);
            fill(243, 205, 156);
            if (hovered)
                fill(colourHover);
            rect(pos.x * width, pos.y * height, size.x * width, size.y * height);
            String t = "Remove";
            textFont(fontBtn);
            fill(136, 70, 36);
            text(t, (pos.x + size.x / 2) * width - textWidth(t) / 2, (pos.y + size.y * 0.8f) * height);
        }
    }

    @Override
    public void mousePressed() {
        field.onMouseClick();
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
        //println(key, keyCode, key == CODED);
        if (key == CODED) {
            keyPressed[keyCode] = true;
            keyTriggered.replace(keyCode, true);
        } else {
            keyPressed[key] = true;
            keyTriggered.replace((int) key, true);
        }
    }

    @Override
    public void keyReleased() {
        if (key == CODED) {
            keyPressed[keyCode] = false;
        } else {
            keyPressed[key] = false;
        }
    }
}
