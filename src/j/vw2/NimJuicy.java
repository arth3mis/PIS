package j.vw2;

import herzi.nim.Move;
import herzi.nim.Nim;
import herzi.nim.NimGame;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class NimJuicy extends PApplet {

    static final float MAX_ANGLE_SPEED = 0.5f;
    static final float FOLLOW_OFFSET = 42;
    static final float STICK_DISTANCE = 64;
    static PImage CURSOR_LASSO;
    static final float ROW_HEIGHT = 135f;
    static final float DIFFICULTY = 1; // 1 EASY - 42 HIGH

    final int COLOR_ROW_HOVER = color(255f, 120);
    final int COLOR_PLAYER = color(242, 237, 220);
    final int COLOR_COMPUTER = color(242, 208, 145);
    final int[] COLOR_STICKS = new int[]{color(151, 109, 242), color(242, 122, 94), color(33, 190, 219), color(30, 118, 120), color(7, 119, 217)};

    ArrayList<Element> allElements = new ArrayList<>();
    ArrayList<Element> pickedElements = new ArrayList<>();
    int shadowColor = color(0, 0, 0, 150f);
    boolean lassoMode = false;
    boolean startNewGame = false;
    boolean won = false;
    PGraphics tailGraphics;

    float playingWidth = 700;
    int rowCount;

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new NimJuicy());
    }

    ArrayList<ArrayList<Stick>> rows;
    NimGame nim;
    Button button;
    int moveRow;
    int moveNumber;
    boolean playerMove;

    public void settings() {
        size(900, 700);
        //fullScreen();
        smooth(3);
    }

    public void setup() {
        CURSOR_LASSO = loadImage("j/vw2/src/cursor_lasso.png");
        noStroke();
        button = new Button(new PVector(800, 600));
        allElements.add(button);
        tailGraphics = createGraphics(width, height);

        startNewGame();
    }

    void startNewGame() {
        // Generate Random Nim-Game
        rowCount = (int) random(2, 5);
        int columnCount = (int) random(2, 8);
        int[] rowArray = new int[rowCount];
        for(int i = 0; i < rowArray.length; i++) {
            rowArray[i] = (int) random(columnCount) + 1;
        }

        // Init values
        moveRow = -1;
        moveNumber = 0;
        playerMove = true;
        int colorIndex = (int) random(COLOR_STICKS.length);
        rows = new ArrayList<>();


        // For-Schleife in SWAG
        // Generates new Sticks
        IntStream.range(0, rowCount).forEach(i -> {
            rows.add(i, new ArrayList<>());
            IntStream.range(0, rowArray[i]).forEach(j -> {
                Stick stick = new Stick(i, new PVector(getRowX(rowArray[i], j), getRowY(i)), colorIndex);
                allElements.add(stick);
                rows.get(i).add(stick);
            });
        });

        // Moves Button to top
        allElements.remove(button);
        allElements.add(button);

        nim = Nim.of(rowArray);
    }

    public void mousePressed() {
        Element mouseElement = getMouseElement();

        if (mouseElement != null) {
            pickElement(mouseElement);

        } else {
            if (pickedElements.size() > 0) {
                // If nothing clicked, drop everything
                pickedElements.forEach(Element::drop);
                pickedElements.clear();
            } else {
                lassoMode = true;
            }
        }
    }

    public void mouseReleased() {
        lassoMode = false;
    }

    // Returns Element on Mouse Position
    public Element getMouseElement() {
        PVector mousePos = new PVector(mouseX, mouseY);
        // Unfortunately returns bottom element;
        //return allElements.stream().filter(s -> s.isOnPosition(mousePos)).findFirst().orElse(null);
        for (int i = allElements.size() - 1; i >= 0; i--) {
            Element e = allElements.get(i);
            if (e.isOnPosition(mousePos)) return e;
        }
        return null;
    }

    // Picks up element
    public void pickElement(Element element) {
        // If something clicked, pick it and maybe drop old Element
        if (pickedElements.size() > 0) {
            // Sticks can be picked up with sticks
            if (element instanceof Stick && pickedElements.get(0) instanceof Stick) {
                float followOffsetLimit = FOLLOW_OFFSET * log(0.5f + 0.5f * pickedElements.size());

                pickedElements.add(element);
                element.followOffset = new PVector(random(-followOffsetLimit, followOffsetLimit), random(-followOffsetLimit, followOffsetLimit));
                element.pick();
            } else {
                if (!lassoMode) {
                    pickedElements.forEach(Element::drop);
                    pickedElements.clear();
                    pickedElements.add(element);
                    element.pick();
                }
            }
        } else {
            pickedElements.add(element);
            element.pick();
        }
    }

    // Draws the Scene
    public void draw() {
        if (startNewGame) {
            startNewGame = false;
            startNewGame();
        }
        if (won) {
            won = false;
            Trophy trophy = new Trophy(new PVector(0.5f * width, 100));
            allElements.add(trophy);
            System.out.println("GGEZ");
        }

        // Logic
        if (lassoMode) {
            Element mouseElement = getMouseElement();
            if (mouseElement != null) {
                pickElement(mouseElement);
            }
        }

        allElements.forEach(Element::update);

        PVector mousePos = new PVector(mouseX, mouseY);
        if (lassoMode) cursor(CURSOR_LASSO);
        else if (allElements.stream().anyMatch(s -> s.isOnPosition(mousePos))) cursor(HAND);
        else cursor(ARROW);

        // DRAWING
        // Background
        background(COLOR_PLAYER);
        rectMode(CORNER);
        noStroke();
        fill(COLOR_COMPUTER);
        rect(0, 0, width, 0.5f *height);

        // Middleground
        if (moveRow != -1) {
            rectMode(CENTER);
            fill(COLOR_ROW_HOVER);
            rect(getRowX(1, 0), getRowY(moveRow), playingWidth, ROW_HEIGHT);
        }

        allElements.forEach(Element::drawShadow);

        PGraphics newTail = createGraphics(width, height);
        newTail.beginDraw();
        newTail.noStroke();
        newTail.tint(255f, 235f);
        newTail.image(tailGraphics, 0, 0);
        allElements.forEach(e -> e.drawTail(newTail));
        newTail.endDraw();

        tint(255f, 200f);
        image(newTail, 0.5f * width, 0.5f * height);
        tailGraphics = newTail;
        tint(255f);

        // Draw Elements
        allElements.forEach(Element::draw);

    }

    // Every draggable element on Screen
    abstract class Element {
        protected boolean moving = false;
        boolean dragged = false;
        PVector pos;
        PVector prevPos;
        PVector vel = new PVector();
        float angle;
        float prevAngle;
        float angleVel;
        float followSpeed = random(0.42f, 2f);
        PVector followOffset = new PVector();

        public Element(PVector pos) {
            this.pos = pos;
        }

        void update() {
            // Drag movement
            if (dragged) {
                vel.mult(0.81f);
                angleVel *= 0.92f;

                PVector mousePos =  new PVector(mouseX, mouseY);
                PVector pMousePos = new PVector(pmouseX, pmouseY);
                PVector direction = PVector.sub(mousePos, PVector.add(followOffset, pos));
                vel.add(PVector.mult(direction, 0.09f * followSpeed));

                // If mouse moved rotate
                if (mousePos.dist(pMousePos) > 8f) {// <--- ???
                    PVector normVel = vel.copy().normalize();
                    PVector normAngle = PVector.fromAngle(angle);
                    angleVel += (normVel.add(normAngle).heading() % 0.5f * PI) * 0.05f;

                }
            }

            if (moving) {
                if (!dragged) bounceWall();

                // Don't let it spin too much
                angleVel = max(min(angleVel, MAX_ANGLE_SPEED), -MAX_ANGLE_SPEED);

                pos.add(vel);
                angle += angleVel;

                // Decay
                vel.mult(0.93f);
                angleVel *= 0.92f;

                // Test if standing still
                if (!dragged && abs(vel.x) < 0.1f && abs(vel.y) < 0.1f && angleVel < 0.1f) {
                    vel = new PVector();
                    angleVel = 0;
                    moving = false;
                    stoppedMoving();
                }
            }
        }

        void draw() {
            drawShape(g, pos, angle);
        }

        void pick() {
            moving = true;
            dragged = true;
        }
        void drop() {
            moving = true;
            dragged = false;
            followOffset = new PVector();
        }

        void drawShadow() {
            tint(color(red(shadowColor), green(shadowColor), blue(shadowColor), alpha(shadowColor)));
            fill(color(red(shadowColor), green(shadowColor), blue(shadowColor), alpha(shadowColor)));
            drawShape(g, PVector.add(pos, new PVector(3, 7)), angle);
            tint(255);
        }

        void drawTail(PGraphics g) {
            if (moving) {
                if (prevPos != null) {
                    int dist = (int) pos.dist(prevPos);
                    PVector difference = PVector.sub(prevPos, pos).div(dist);
                    //float angleDifference = (angle - prevAngle) / dist;
                    fill(100);
                    IntStream.range(0, dist/3).forEach(i -> {
                        drawShape(g, PVector.add(pos, PVector.mult(difference, 3 * i)), angle);
                    });
                }

                prevPos = pos.copy();
                //prevAngle = angle;
                //TODO tint(color(red(shadowColor), green(shadowColor), blue(shadowColor), alpha(shadowColor)));
                fill(100);
                drawShape(g, pos, angle);
            }
        }

        abstract boolean isOnPosition(PVector mousePos);
        abstract void bounceWall();
        abstract void drawShape(PGraphics g, PVector pos, float angle);
        void stoppedMoving() {}

    }

    class Stick extends Element {
        static final float length = 105f;
        static final float weight = 30f;

        boolean active = true;
        int row;
        int col;
        PVector resetPos;
        float resetAngle;

        public Stick(int row, PVector pos, int colorIndex) {
            super(pos);
            this.row = row;
            col = COLOR_STICKS[colorIndex];
            colorMode(HSB);
            col = color(hue(col), saturation(col) + random(-10, 10), brightness(col) + random(-50, 50));
            colorMode(RGB);

            angle = random(-0.05f*PI, 0.05f*PI);
            resetAngle = angle;
            prevAngle = angle;
            resetPos = pos;
        }

        public void update() {
            super.update();
            if (!dragged) {
                if (row != -1) {
                    if (!isInRow(pos, row)) {
                        float rowX = getRowX(1, 0);
                        float rowY = getRowY(row);
                        if (pos.x < rowX - 0.5f * playingWidth)
                            vel.add(0.03f * (rowX - 0.5f * playingWidth - pos.x), 0);
                        if (pos.x > rowX + 0.5f * playingWidth)
                            vel.add(0.03f * (rowX + 0.5f * playingWidth - pos.x), 0);
                        if (pos.y < rowY - 0.5f * ROW_HEIGHT) vel.add(0, 0.03f * (rowY - 0.5f * ROW_HEIGHT - pos.y));
                        if (pos.y > rowY + 0.5f * ROW_HEIGHT) vel.add(0, 0.03f * (rowY + 0.5f * ROW_HEIGHT - pos.y));
                        moving = true;
                    } else if (moving) {
                        vel.mult(0.95f);
                        angleVel *= 0.95f;
                    }
                }
            }
        }

        public void draw() {
            fill(col, active ? 255f : 160f);
            super.draw();

        }

        @Override
        void drawShadow() {
            if (active) {
                super.drawShadow();
            }
        }

        boolean isOnPosition(PVector mousePos) {
            if (dragged) return false;
            if (moveRow != -1 && moveRow != row && active) return false;
            PVector testPos = PVector.sub(pos, mousePos);
            testPos.rotate(-angle);
            return (abs(testPos.x) <= 0.5f*weight && abs(testPos.y) <= 0.5f*length);
        }

        @Override
        void pick() {
            super.pick();
            if (row != -1 && moveRow != row) {
                moveRow = row;
            }
        }

        @Override
        void drop() {
            super.drop();
            float dropRandom = (pickedElements.size() == 1 ? 0.5f : 2f) * sqrt(pickedElements.size());
            vel.add(PVector.fromAngle(random(0, 2 * PI)).setMag(dropRandom));
            if (angleVel < 0.5f) {
                angleVel = (pickedElements.size() == 1 ? 0.1f : 0.85f) * random(-MAX_ANGLE_SPEED, MAX_ANGLE_SPEED);
            } else {
                angleVel *= pickedElements.size() == 1 ? 1f : random(1, 3);
            }

            if (isInRow(new PVector(mouseX, mouseY), moveRow)) {
                if (row == -1 && moveNumber > 0) {
                    addToRow(moveRow);
                }
            } else if (row != -1 && row == moveRow) {
                removeFromRow();
            }

            // Reset Move
            if (moveNumber == 0) {
                moveRow = -1;
            }
        }

        @Override
        void bounceWall() {
            // Bounce on wall
            if (pos.x - 0.5 * weight < 0) {
                pos.x = weight - pos.x;
                vel.x *= -1;
            }
            if (pos.y - 0.5 * weight < 0) {
                pos.y = weight - pos.y;
                vel.y *= -1;
            }
            if (pos.x + 0.5 * weight > width) {
                pos.x = 2 * width - weight - pos.x;
                vel.x *= -1;
            }
            if (pos.y + 0.5 * weight > height) {
                pos.y = 2 * height - weight - pos.y;
                vel.y *= -1;
            }
        }

        @Override
        void drawShape(PGraphics g, PVector pos, float angle) {
            rotatedRect(g, pos.x, pos.y, weight, length, angle);
        }

        void addToRow(int row) {
            removeFromRow();
            this.row = row;
            rows.get(row).add(this);
            active = true;
            moveNumber--;
        }

        void removeFromRow() {
            if (row > -1) {
                rows.get(row).remove(this);
                row = -1;
                active = false;
                moveNumber++;
            }
        }
    }

    class Button extends Element {

        PImage image;
        float radius;

        public Button(PVector pos) {
            super(pos);
            image = loadImage("j/vw2/src/your_turn.png");
            radius = image.height * 0.5f;
            followSpeed = 1.3f;
        }

        @Override
        void draw() {
            super.draw();
        }

        @Override
        void drawTail(PGraphics g) {
            if (moving) {
                if (prevPos != null) {
                    int dist = (int) pos.dist(prevPos);
                    PVector difference = PVector.sub(prevPos, pos).div(dist);
                    //float angleDifference = (angle - prevAngle) / dist;
                    fill(100);
                    IntStream.range(0, dist/3).forEach(i -> {
                        g.circle(pos.x + 3 * i * difference.x, pos.y + 3 * i * difference.y, 2 * radius);
                    });
                }

                prevPos = pos.copy();
                //prevAngle = angle;
                //TODO tint(color(red(shadowColor), green(shadowColor), blue(shadowColor), alpha(shadowColor)));
                fill(100);
                g.circle(pos.x, pos.y, 2 * radius);
            }
        }

        @Override
        void stoppedMoving() {
            if (pos.y < height * 0.5f) {
                if (moveRow > -1 && moveNumber > 0) {
                    Move move = Move.of(moveRow, moveNumber);
                    nim = nim.play(move);

                    if (nim.isGameOver()) {
                        won = true;

                    } else {
                        Move computerMove;
                        // Make Computer move
                        if (0.15f / DIFFICULTY > random(1)) {
                            computerMove = nim.randomMove();
                        } else {
                            computerMove = nim.bestMove();
                        }
                        for (int i = 0; i < computerMove.number; i++) {
                            Stick stick = rows.get(computerMove.row).get((int) random(rows.get(computerMove.row).size()));
                            stick.removeFromRow();
                            stick.vel = new PVector(random(-50, 50), random(-50, 50));
                            stick.moving = true;
                        }

                        nim = nim.play(computerMove);
                        moveRow = -1;
                        moveNumber = 0;

                        if (nim.isGameOver()) {
                            startNewGame = true;
                        }
                    }

                    vel = new PVector(random(-5, 5), 27f);
                    angleVel = 0.8f * random(-MAX_ANGLE_SPEED, MAX_ANGLE_SPEED);
                    moving = true;

                } else {
                    vel = new PVector(random(-5, 5), 27f);
                    angleVel = 0.8f * random(-MAX_ANGLE_SPEED, MAX_ANGLE_SPEED);
                    moving = true;
                }
            }
        }

        @Override
        void drop() {
            super.drop();
        }

        @Override
        boolean isOnPosition(PVector mousePos) {
            if (dragged) return false;
            return dist(pos.x, pos.y, mousePos.x, mousePos.y) < radius;
        }

        @Override
        void bounceWall() {
            // Bounce on wall
            if (pos.x - radius < 0) {
                pos.x = 2 * radius - pos.x;
                vel.x *= -1;
            }
            if (pos.y - radius < 0) {
                pos.y = 2 * radius - pos.y;
                vel.y *= -1;
            }
            if (pos.x + radius > width) {
                pos.x = 2 * width - 2 * radius - pos.x;
                vel.x *= -1;
            }
            if (pos.y + radius > height) {
                pos.y = 2 * height - 2 * radius - pos.y;
                vel.y *= -1;
            }
        }

        @Override
        void drawShape(PGraphics g, PVector pos, float angle) {
            rotatedImage(g, image, pos.x, pos.y, angle);
        }
    }

    class Trophy extends Element {

        PImage image;

        public Trophy(PVector pos) {
            super(pos);
            image = loadImage("j/vw2/src/trophy.png");
            vel = new PVector(0, 17f);
            moving = true;
        }

        @Override
        void draw() {
            super.draw();
        }

        @Override
        void drawTail(PGraphics g) {
        }

        @Override
        void drop() {
            super.drop();
            if (nim.isGameOver()) startNewGame = true;
        }

        @Override
        boolean isOnPosition(PVector mousePos) {
            if (dragged) return false;
            PVector testPos = PVector.sub(pos, mousePos);
            testPos.rotate(-angle);
            return (abs(testPos.x) <= 32 && abs(testPos.y) <= 64);
        }

        @Override
        void bounceWall() {
            // Bounce on wall
            if (pos.x - 32 < 0) {
                pos.x = 2 * 32 - pos.x;
                vel.x *= -1;
            }
            if (pos.y - 32 < 0) {
                pos.y = 2 * 32 - pos.y;
                vel.y *= -1;
            }
            if (pos.x + 32 > width) {
                pos.x = 2 * width - 2 * 32 - pos.x;
                vel.x *= -1;
            }
            if (pos.y + 32 > height) {
                pos.y = 2 * height - 2 * 32 - pos.y;
                vel.y *= -1;
            }
        }

        @Override
        void drawShape(PGraphics g, PVector pos, float angle) {
            rotatedImage(g, image, pos.x, pos.y, angle);
        }
    }

    void rotatedRect(PGraphics g, float x, float y, float width, float height, float angle) {
        g.rectMode(CENTER);
        g.translate(x, y);
        g.rotate(angle);
        g.rect(0, 0, width, height);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    void rotatedImage(PGraphics g, PImage img, float x, float y, float angle) {
        g.imageMode(CENTER);
        g.translate(x, y);
        g.rotate(angle);
        g.image(img, 0, 0);
        g.rotate(-angle);
        g.translate(-x, -y);
    }

    float getRowY(int rowNumber) {
        return (0.5f * height) - (0.5f * (rowCount-1) * ROW_HEIGHT) + (rowNumber * ROW_HEIGHT);
    }

    float getRowX(int stickCount, int stickNumber) {
        float rowWidth = stickCount == 1 ? 0 : ((2 * playingWidth / PI) * atan(0.5f * PI * STICK_DISTANCE * (stickCount-1) / playingWidth)) / (stickCount-1);
        return (0.5f * width) - (0.5f * (stickCount-1) * rowWidth) + (stickNumber * rowWidth);
    }

    boolean isInRow(PVector vector, int rowNumber) {
        float rowX = getRowX(1, 0);
        float rowY = getRowY(rowNumber);
        if (vector.x < rowX - 0.5f * playingWidth) return false;
        if (vector.x > rowX + 0.5f * playingWidth) return false;
        if (vector.y < rowY - 0.5f * ROW_HEIGHT) return false;
        if (vector.y > rowY + 0.5f * ROW_HEIGHT) return false;
        return true;
    }

}
