package a.vw2;

import herzi.nim.Move;
import herzi.nim.Nim;
import herzi.nim.NimGame;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.*;

class vw2_nim extends PApplet {

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
    int selectRow;
    int winner;
    Field field;
    Button btnRemove;

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
        selectRow = -1;
        winner = -1;
        // display
        field = new Field(gameRows);
        btnRemove = new Button(-0.04f, -0.02f, -0.2f, -0.05f, ' ');
    }

    void update() {
        if (processKeyTrigger(BACKSPACE)) {
            resetGame();
        }
        if (processKeyTrigger(btnRemove.assignedKey)) {
            btnRemove.triggered = true;
        }
        // arrow keys
        int[] moveHovered = {0, 0};
        if (processKeyTrigger(UP)) {
            moveHovered[0]--;
        }
        if (processKeyTrigger(LEFT)) {
            moveHovered[1]--;
        }
        if (processKeyTrigger(DOWN)) {
            moveHovered[0]++;
        }
        if (processKeyTrigger(RIGHT)) {
            moveHovered[1]++;
        }

        if (mouseMovedAfterKey) {
            Stick s = field.checkHover(mouseX, mouseY);
            field.setStickHovered(s);
        } else {
            field.moveStickHovered(moveHovered);
        }

        btnRemove.checkHover(mouseX, mouseY);


        // mouse click
        if (processMouseTrigger(LMB))
            ;


        // remove button
        if (btnRemove.triggered) {
            btnRemove.triggered = false;
            game = game.play(Move.of(selectRow, field.selectCount));
            field.removeSelected(selectRow);
            if (game.isGameOver())
                winner = 0;
            else {
                // computer move
                Move m = game.bestMove();
                game = game.play(m);
                field.removeRandom(m);
                if (game.isGameOver())
                    winner = 1;
            }
        }


//        if (mouseHover)
//            toggleSelectHovered();
//        // reset select row
//        if (Arrays.stream(sticks).noneMatch(s -> s.selected))
//            selectRow = -1;
//        field.update(mouseX, mouseY);
    }

    @Override
    public void draw() {
        update();
        background(222, 184, 135);
        field.draw();
    }


    class Field {
        Stick[][] sticks;
        Stick stickHovered, stickHoveredPrev;  // changed by mouse/keys
        int selectCount;

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
            sticks = new Stick[rows.length][];
            for (int i = 0; i < rows.length; i++) {
                sticks[i] = new Stick[rows[i]];
                float xWidth = xStickSpace * rows[i] - (xStickSpace - xStickSize);  // check with formula above
                for (int j = 0; j < rows[i]; j++) {
                    sticks[i][j] = new Stick(
                            0.5f - xWidth / 2 + xStickSpace * j,
                            yPadding + yStickSpace * i,
                            xStickSize, yStickSize,
                            0.15f);
                }
            }
        }

        void setStickHovered(Stick s) {
            stickHoveredPrev = stickHovered;
            stickHovered = s;
        }

        void moveStickHovered(int[] m) {

        }

        void toggleSelectHovered() {
//            if (stickHovered != null && (stickHovered.row == selectRow || selectRow == -1)) {
//                stickHovered.selected = !stickHovered.selected;
//                selectRow = stickHovered.row;
//            }
        }

        void removeSelected(int selectRow) {
            Arrays.stream(sticks[selectRow]).filter(s -> s.selected).forEach(s -> {
                s.selected = false;
                s.removed = true;
            });
        }

        void removeRandom(Move m) {
            List<Stick> ls = new ArrayList<>(Arrays.stream(sticks[m.row]).filter(s -> !s.removed).toList());
            Collections.shuffle(ls);
            ls.stream().limit(m.number).forEach(s -> {
                s.selected = false;
                s.removed = true;
            });
        }

        Stick checkHover(float mouseX, float mouseY) {
            for (Stick[] stick : sticks) {
                for (Stick s : stick) {
                    if (s.checkHovered(mouseX, mouseY))
                        return s;
                }
            }
            return null;
        }

        void draw() {
            for (int i = 0; i < sticks.length; i++) {
                for (int j = 0; j < sticks[i].length; j++) {
                    sticks[i][j].draw(sticks[i][j] == stickHovered, i == selectRow);
                }
            }
            btnRemove.draw();
        }
    }


    class Stick {
        // logic
        boolean selected;
        boolean removed;
        // display
        PVector pos;
        PVector size;  // includes border
        float borderPercent;  // border size relative to size.x
        int colour = color(205, 133, 63);
        int borderColour = color(150, 82, 45);//color(139, 69, 19);
        float removeAnimation;

        Stick(float x, float y, float sx, float sy, float borderPercent) {
            pos = new PVector(x, y);
            size = new PVector(sx, sy);
            this.borderPercent = borderPercent;
            removeAnimation = 1;
        }

        boolean checkHovered(float mouseX, float mouseY) {
            if (removed) return false;
            return mouseX >= pos.x * width && mouseX < (pos.x+size.x) * width &&
                   mouseY >= pos.y * height && mouseY < (pos.y+size.y) * height;
        }

        void update() {
            if (removed && removeAnimation > 0) {
                removeAnimation = max(removeAnimation - 0.03f, 0);
            }
        }

        void draw(boolean hovered, boolean legalMove) {
            noStroke();
            fill(hovered ?
                    (selected ? colourHoverSelected : (legalMove ? colourHover : colourHoverIllegal)) :
                    (selected ? colourSelected : borderColour),
                    removeAnimation*255);
            rect(pos.x * width, pos.y * height, size.x * width, size.y * height);
            fill(colour, removeAnimation*255);
            rect((pos.x + size.x*borderPercent) * width, (pos.y + size.x*borderPercent) * height,
                    (size.x - 2*size.x*borderPercent) * width, (size.y - 2*size.x*borderPercent) * height);
        }
    }

    class Button {
        // logic
        boolean hovered;
        boolean triggered;
        int assignedKey;
        // display
        PVector pos;
        PVector size;
        PFont fontBtn;

        /**negative values are relative to right/bottom*/
        Button(float x, float y, float sx, float sy, int assignedKey) {
            if (x < 0) x = 1 + x + (sx < 0 ? sx : 0);
            if (y < 0) y = 1 + y + (sy < 0 ? sy : 0);
            pos = new PVector(x, y);
            size = new PVector(abs(sx), abs(sy));
            this.assignedKey = assignedKey;
            fontBtn = createFont("Arial black", size.y * height * 0.7f);
        }

        boolean checkHover(float mouseX, float mouseY) {
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

    final int LMB = 0, RMB = 1, XMB = 2;
    boolean[] mousePressed = new boolean[3];
    Map<Integer, Boolean> mouseTriggered = new HashMap<>();
    boolean mouseMovedAfterKey = true;

    boolean processMouseTrigger(int n) {
        if (mouseTriggered.get(n)) {
            mouseTriggered.replace(n, false);
            return true;
        }
        return false;
    }

    @Override
    public void mousePressed() {
        handleMouse(true);
    }

    @Override
    public void mouseReleased() {
        handleMouse(false);
    }

    void handleMouse(boolean b) {
        if (mouseButton == LEFT) {
            mousePressed[LMB] = b;
            mouseTriggered.replace(LMB, b);
        } else if (mouseButton == RIGHT) {
            mousePressed[RMB] = b;
            mouseTriggered.replace(RMB, b);
        } else {
            mousePressed[XMB] = b;
            mouseTriggered.replace(XMB, b);
        }
    }

    @Override
    public void mouseMoved() {
        mouseMovedAfterKey = true;
    }

    float mouseWheelDelta = 0;

    @Override
    public void mouseWheel(MouseEvent event) {
        mouseWheelDelta += event.getCount();
    }

    boolean[] keyPressed = new boolean[1000];
    Map<Integer, Boolean> keyTriggered = new HashMap<>();

    boolean processKeyTrigger(int n) {
        if (keyTriggered.get(n)) {
            keyTriggered.replace(n, false);
            return true;
        }
        return false;
    }

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
        mouseMovedAfterKey = false;
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
