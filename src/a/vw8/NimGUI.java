package a.vw8;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.*;
import java.util.stream.IntStream;

class NimGUI extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new NimGUI());
    }

    @Override
    public void settings() {
        //fullScreen();
        size(800, 900);
        smooth(8);
    }

    enum State {
        START, PLAYER1, PLAYER2, BOT2, WINNER1, WINNER2;

        boolean isWin() { return this == WINNER1 || this == WINNER2; }
        boolean isHuman() { return this == PLAYER1 || this == PLAYER2; }

        State nextTurn(boolean botPlayer2) {
            assert this == PLAYER1 || this == PLAYER2 || this == BOT2;
            if (this == PLAYER1) return botPlayer2 ? BOT2 : PLAYER2;
            else return PLAYER1;
        }

        State win() {
            assert this == PLAYER1 || this == PLAYER2 || this == BOT2;
            return this == PLAYER1 ? WINNER1 : WINNER2;
        }

        State restart() {
            return PLAYER1;
        }
    }

    // variables
    //
    State state = State.START;
    int rowChoice, maxColumns;
    int[] gameRows;
    NimGame game;
    boolean botPlayer2, botBestMove;
    // display & user interaction
    float botMoveDelay, botMoveDelaySpeed = 0.05f;
    Field field;
    Button btnRemove;
    List<Button> buttons;  // easy for-each update/draw

    PFont winnerFont;
    float winnerAnimation;
    int colourHover = color(250, 235, 215);
    int colourHoverIllegal = color(50, 110, 120);
    int colourHoverSelected = color(240, 30, 10);
    int colourSelected = 0xff8b0000;
    // sticks
    int borderColour = color(150, 82, 45);//color(139, 69, 19);
    int stickColour = color(205, 133, 63);

    @Override
    public void setup() {
        frameRate(60);
        winnerFont = createFont("Serif.bolditalic", 50);
        buttons = new ArrayList<>();
        btnRemove = new Button(-0.04f, -0.02f, -0.2f, -0.05f, ENTER);
        buttons.add(btnRemove);
        // setup input triggers
        keyTriggered.put(UP, false);
        keyTriggered.put(DOWN, false);
        keyTriggered.put(LEFT, false);
        keyTriggered.put(RIGHT, false);
        keyTriggered.put((int)' ', false);
        keyTriggered.put((int)ENTER, false);
        keyTriggered.put((int)BACKSPACE, false);
        keyTriggered.put((int)'r', false);
        keyTriggered.put((int)'p', false);
        keyTriggered.put((int)'1', false);
        keyTriggered.put((int)'2', false);
        keyTriggered.put((int)'3', false);
        keyTriggered.put((int)'4', false);
        keyTriggered.put((int)'5', false);
        keyTriggered.put((int)'6', false);
        keyTriggered.put((int)'7', false);
        keyTriggered.put((int)'8', false);
        keyTriggered.put((int)'9', false);
        keyTriggered.put(SHIFT, false);
        mouseTriggered.put(LMB, false);

        rowChoice = 4;
        maxColumns = 5;
        botPlayer2 = true;
        botBestMove = true;
        resetGame();
    }

    void resetGame() {
        gameRows = IntStream.range(0, rowChoice).map(i -> new Random().nextInt(1, maxColumns+1)).toArray();
        game = Nim.of(gameRows);
        state = state.restart();
        // display
        field = new Field(gameRows);
        botMoveDelay = -1;
        winnerAnimation = -1;
    }

    void update() {
        if (processKeyTrigger(BACKSPACE))
            resetGame();
        if (processKeyTrigger(btnRemove.assignedKey))
            btnRemove.triggered = true;
        if (processKeyTrigger('p'))
            if (state == State.PLAYER1) botPlayer2 = !botPlayer2;
        if (processKeyTrigger('r'))
            botBestMove = !botBestMove;
        for (int i = '1'; i <= '9'; i++) {
            if (processKeyTrigger(i)) {
                if (processKeyTrigger(SHIFT))
                    maxColumns = i - '0';
                else
                    rowChoice = i - '0';
            }
        }
        // arrow keys
        int[] keyMoves = {0, 0};
        if (processKeyTrigger(UP))
            keyMoves[0]--;
        if (processKeyTrigger(LEFT))
            keyMoves[1]--;
        if (processKeyTrigger(DOWN))
            keyMoves[0]++;
        if (processKeyTrigger(RIGHT))
            keyMoves[1]++;

        // update field with input
        field.update(mouseX, mouseY, keyMoves);
        // update buttons
        boolean lmb = processMouseTrigger(LMB);
        for (Button b : buttons)
            if (b.update(mouseX, mouseY, lmb))
                lmb = false;
        // select stick
        if (lmb || processKeyTrigger(' ')) {
            field.toggleSelected();
        }
        // turn ended?
        if (!state.isWin()) {
            boolean turn = false;
            if (state.isHuman() && btnRemove.triggered && field.playerMove != null) {
                turn = playerMove();
            } else if (!state.isHuman()) {
                if (botMoveDelay >= 0) {
                    botMoveDelay = -1;
                    turn = botMove();
                } else
                    botMoveDelay += botMoveDelaySpeed;
            }
            btnRemove.triggered = false;
            if (turn)
                state = game.isGameOver() ? state.win() : state.nextTurn(botPlayer2);
        }
    }

    boolean playerMove() {
        game = game.play(field.playerMove);
        // display
        field.removeSelected();
        field.playerMove = null;
        return true;
    }

    boolean botMove() {
        Move m = botBestMove ? game.bestMove() : game.randomMove();
        game = game.play(m);
        // display
        field.removeRandom(m);
        return true;
    }

    @Override
    public void draw() {
        update();
        background(222, 184, 135);
        field.draw();
        buttons.forEach(Button::draw);
        drawInfo();
        if (state.isWin())
            drawWinner();
    }

    void drawInfo() {
        fill(borderColour);
        textFont(winnerFont, 0.04f * width);
        float x = 0.02f * width, y = 0.05f * width;
        if (!botPlayer2)
            text("Turn: player "+(state == State.PLAYER1 ? 1 : 2), x, y);
        else if (!botBestMove)
            text("Bot: random move", x, y);
    }

    void drawWinner() {
        float sy = 0.2f;
        String t1 = "Player "+(state == State.WINNER1 ? 1 : 2)+(state == State.WINNER2 && botPlayer2 ? " (bot)" : "")+" won!";
        String t2 = "Press [Backspace] to restart";
        winnerAnimation = min(1, winnerAnimation + 0.03f);
        fill(borderColour, max(0, winnerAnimation * 255));
        textFont(winnerFont, 0.4f*sy*height);
        text(t1, width/2f - textWidth(t1)/2, height/2f - 0.05f*sy*height);
        float t2Size = 0.2f*sy*height;
        textFont(winnerFont, t2Size);
        text(t2, width/2f - textWidth(t2)/2, height/2f + 0.05f*sy*height + t2Size);
    }


    class Field {
        Stick[][] sticks;
        Stick stickHovered;  // mouse
        Stick stickActive, stickActivePrev;  // mouse & keyboard
        Move playerMove;

        Field(int[] rows) {
            assert rows != null && rows.length > 0;
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
                    sticks[i][j] = new Stick(i, j,
                            0.5f - xWidth / 2 + xStickSpace * j,
                            yPadding + yStickSpace * i,
                            xStickSize, yStickSize,
                            0.15f);
                }
            }
            stickActivePrev = sticks[0][0];
            playerMove = null;
        }

        void update(float mouseX, float mouseY, int[] keyMoves) {
            stickHovered = null;
            // loop sticks
            for (Stick[] stick : sticks) {
                for (Stick s : stick) {
                    s.update();
                    // mouse hover
                    if (s.checkHovered(mouseX, mouseY)) {
                        stickHovered = s;
                    }
                }
            }
            // mouse overwrites keyboard?
            if (mouseMovedAfterKey && stickHovered != stickActive) {
                stickActivePrev = stickActive;
                stickActive = stickHovered;
                return;
            }
            // keyboard
            if (keyMoves[0] != 0 || keyMoves[1] != 0) {
                if (stickActive == null) {
                    stickActive = stickActivePrev;
                } else {
                    int newRow = min(sticks.length - 1, max(0, stickActive.row + keyMoves[0]));
                    int newCol = min(sticks[newRow].length - 1, max(0, stickActive.col + keyMoves[1]));
                    // do not select removed sticks
                    while (sticks[newRow][newCol].removed) {
                        if (keyMoves[0] != 0) {
                            newRow = (newRow + keyMoves[0] + sticks.length) % sticks.length;
                            newCol = min(newCol, sticks[newRow].length - 1);
                            while (sticks[newRow][newCol].removed)
                                newCol = (newCol + 1 + sticks[newRow].length) % sticks[newRow].length;
                        }
                        if (keyMoves[1] != 0) {
                            newCol = (newCol + keyMoves[1] + sticks[newRow].length) % sticks[newRow].length;
                        }
                    }
                    stickActive = sticks[newRow][newCol];
                }
            }
        }

        void toggleSelected() {
            if (stickActive == null)
                return;
            // deselect
            if (stickActive.selected) {
                stickActive.selected = false;
                if (playerMove.number == 1)
                    playerMove = null;
                else
                    playerMove = Move.of(stickActive.row, playerMove.number - 1);
            }
            // select
            else {
                if (playerMove == null || playerMove.row == stickActive.row) {
                    stickActive.selected = true;
                    playerMove = Move.of(stickActive.row, playerMove == null ? 1 : playerMove.number + 1);
                }
            }
        }

        void removeSelected() {
            Arrays.stream(sticks[playerMove.row]).filter(s -> s.selected).forEach(s -> {
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
                s.removeAnimation *= 2;
            });
        }

        void draw() {
            for (int i = 0; i < sticks.length; i++) {
                for (int j = 0; j < sticks[i].length; j++) {
                    sticks[i][j].draw(sticks[i][j] == stickActive, playerMove == null || i == playerMove.row);
                }
            }
        }
    }


    class Stick {
        // logic
        int row, col;
        boolean selected;
        boolean removed;
        // display
        PVector pos;
        PVector size;  // includes border
        float borderPercent;  // border size relative to size.x
        int colour;
        float removeAnimation;

        Stick(int row, int col, float x, float y, float sx, float sy, float borderPercent) {
            this.row = row;
            this.col = col;
            pos = new PVector(x, y);
            size = new PVector(sx, sy);
            this.borderPercent = borderPercent;
            removeAnimation = 1;
            colour = stickColour + (round(random(-20, 10)) << 16);
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

        boolean update(float mouseX, float mouseY, boolean triggered) {
            hovered = mouseX >= pos.x * width && mouseX < (pos.x + size.x) * width &&
                             mouseY >= pos.y * height && mouseY < (pos.y + size.y) * height;
            if (hovered) this.triggered = triggered;
            return hovered;
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
        if (mouseTriggered.containsKey(n) && mouseTriggered.get(n)) {
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
        if (keyTriggered.containsKey(n) && keyTriggered.get(n)) {
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
