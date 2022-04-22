package aj_vw1;

import processing.core.*;

public class ArtSketch extends PApplet {
    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new ArtSketch());
    }

    int border = 0;

    Star[] stars = new Star[100];
    int blinkTimer;
    Tree tree;

    PVector shake = new PVector(0, 0);
    static final float shakeAmount = 1.8f;
    float t = 0;


    int[] starColors = new int[]{
            color(255, 255, 255),
            color(255, 255, 255),
            color(255, 255, 255),
            color(255, 255, 255),
            color(230, 230, 230),
            color(200, 200, 200),
            color(230, 230, 255),
            color(255, 255, 230)
    };

    public void settings() {
        size(800, 700);
    }

    public void setup() {
        //frameRate(60);
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(starColors[i % starColors.length]);
        }
        blinkTimer = round(random(5));
        tree = new Tree();
    }

    public void draw() {
        t += 0.4;
        update();
        // draw
        //background(0, 0, 30, 0.9f);
        noStroke();
        stroke(150, 40, 10);
        strokeWeight(border*2);
        fill(0, 0, 25, 255-tree.flameSize*225);
        rect(0, 0, width-2*border, height-2*border);


        if (tree.flameSize > 0) {
            shake.x = shakeAmount* tree.flameSize*sin(2.1f*t);
            shake.y = shakeAmount* tree.flameSize*sin(2.941f*t);
        }

        for (Star b : stars) {
            b.draw();
        }
        tree.draw();
    }

    public void update() {
        for (Star b : stars) {
            b.update(stars);
        }
        blinkTimer--;
        if (blinkTimer <= 0) {
            Star s;
            do {
                int i = (int)random(stars.length);
                s = stars[i];
            } while (s.blinking > 0);
            s.reverseBlink = false;
            s.blinkSpeed = random(0.03f, 0.06f);
            s.blinking = s.blinkSpeed/10;
            blinkTimer = round(random(frameRate/2, frameRate*2));
        }
        tree.update();
    }

    class Tree {
        PImage img = loadImage("tree.png");
        PVector imgSize = new PVector(444, 468);
        float factor = 0.7f;
        PVector size;
        float MAX_Y;
        PVector p;
        PVector v;
        float speed = 0.17f;

        static final float treeOffsetX = 161;
        static final float treeOffsetY = 205;

        int a = 100;
        float x = 190;
        float s = 0.6f;
        float flameSize = 0f;

        Tree() {
            size = new PVector(imgSize.x, imgSize.y);
            size.mult(factor);
            MAX_Y = height - size.y + 20 * factor;
            p = new PVector(width/2 - size.x/2, MAX_Y);
            v = new PVector();
        }

        void draw() {

            // Flame size
            if (keyPressed && key == CODED) {
                if (keyCode == UP) {
                    if (tree.flameSize < 0.98f) tree.flameSize += (1 - tree.flameSize) * 0.05f;
                    else if (tree.flameSize < 1f) tree.flameSize = 1f;
                    tree.v.y -= tree.speed;
                }
                if (keyCode == DOWN) {
                    if (tree.flameSize > 0.02f) tree.flameSize -= tree.flameSize * 0.4f;
                    else if (tree.flameSize > 0f) tree.flameSize = 0f;
                    tree.v.y += 0.5*tree.speed;
                }
            } else {
                if (tree.flameSize > 0.02f) tree.flameSize -= tree.flameSize * 0.04f;
                else if (tree.flameSize > 0f) tree.flameSize = 0f;
            }


            tint(230,180,255);
            // if flame active
            if (flameSize > 0) {

                x = 190 + (sin(t)-0.5f) * 5;

                a = 100;
                fill(255,100,0);
                beginShape();
                curveVertex(treeOffsetX+shake.x+p.x                 , treeOffsetY+ shake.y+p.y+a+flameSize*(x));
                curveVertex(treeOffsetX+shake.x+p.x                 , treeOffsetY+ shake.y+p.y+a+flameSize*(x));
                curveVertex(treeOffsetX+shake.x+p.x-flameSize*50    , treeOffsetY+ shake.y+p.y+a+flameSize*(50));
                curveVertex(treeOffsetX+shake.x+p.x                 , treeOffsetY+ shake.y+p.y+a+flameSize*(0));
                curveVertex(treeOffsetX+shake.x+p.x+flameSize*50    , treeOffsetY+ shake.y+p.y+a+flameSize*(50));
                curveVertex(treeOffsetX+shake.x+p.x                 , treeOffsetY+ shake.y+p.y+a+flameSize*(x));
                curveVertex(treeOffsetX+shake.x+p.x                 , treeOffsetY+ shake.y+p.y+a+flameSize*(x));
                endShape();

                x = 190 + (sin(0.9f*t+2)-0.5f) * 5;
                a = 110;
                fill(255,200,10);
                beginShape();
                curveVertex(treeOffsetX+shake.x+p.x                 ,treeOffsetY+shake.y+p.y+a+flameSize*(s*x));
                curveVertex(treeOffsetX+shake.x+p.x                 ,treeOffsetY+shake.y+p.y+a+flameSize*(s*x));
                curveVertex(treeOffsetX+shake.x+p.x-flameSize*s*50  ,treeOffsetY+shake.y+p.y+a+flameSize*(s*50));
                curveVertex(treeOffsetX+shake.x+p.x                 ,treeOffsetY+shake.y+p.y+a);
                curveVertex(treeOffsetX+shake.x+p.x+flameSize*s*50  ,treeOffsetY+shake.y+p.y+a+flameSize*(s*50));
                curveVertex(treeOffsetX+shake.x+p.x                 ,treeOffsetY+shake.y+p.y+a+flameSize*(s*x));
                curveVertex(treeOffsetX+shake.x+p.x                 ,treeOffsetY+shake.y+p.y+a+flameSize*(s*x));
                endShape();
            }

            image(img, p.x + shake.x, p.y + shake.y, size.x, size.y);
        }

        void update() {
            // If not on Ground: Gravity
            if (p.y < MAX_Y) {
                v.y += 0.07f;
            }

            p.add(v);

            // COllisions
            if (p.y < -20 * factor) {
                p.y = -20 * factor;
                v.y = 0;
            } else if (p.y > MAX_Y) {
                p.y = MAX_Y;
                v.y = 0;
            }
        }
    }

    class Star {
        float radius;
        int colour;
        PVector p;
        PVector v;

        float blinking, blinkSpeed;
        boolean reverseBlink;

        Star(int c) {
            colour = c;
            // randomise size, initial position and velocity
            radius = random(1, 3);
            p = new PVector(
                    random(border + radius, width - border - radius),
                    random(border + radius, height - border - radius));
            v = new PVector(
                    random(0.2f) * (random(-1, 1) > 0 ? 1 : -1),
                    random(0.2f) * (random(-1, 1) > 0 ? 1 : -1));
        }

        void draw() {
            noStroke();
            fill(colour);
            ellipse(p.x + shake.x, p.y + shake.y, radius*2, radius*2);
            if (blinking > 0)
                drawBlink();
        }

        void drawBlink() {
            pushMatrix();
            translate(p.x, p.y);
            rotate(-PI/7 + QUARTER_PI * blinking);
            float size = radius*0.8f;
            float len = radius*7;
            if (reverseBlink) {
                len *= 2 - blinking;
            } else {
                len *= blinking;
            }
            fill(255);
            beginShape();
            curveVertex(len, 0);
            curveVertex(len, 0);
            curveVertex(size, size);
            curveVertex(0, len);
            curveVertex(0, len);
            vertex(0, len);
            curveVertex(0, len);
            curveVertex(0, len);
            curveVertex(-size, size);
            curveVertex(-len, 0);
            curveVertex(-len, 0);
            vertex(-len, 0);
            curveVertex(-len, 0);
            curveVertex(-len, 0);
            curveVertex(-size, -size);
            curveVertex(0, -len);
            curveVertex(0, -len);
            vertex(0, -len);
            curveVertex(0, -len);
            curveVertex(0, -len);
            curveVertex(size, -size);
            curveVertex(len, 0);
            curveVertex(len, 0);
            endShape();
            popMatrix();
        }

        void update(Star[] others) {
            p.add(v);
            // blink
            if (blinking > 2) blinking = 0;
            if (blinking > 1) reverseBlink = true;
            if (blinking > 0) blinking += blinkSpeed;
            // collision with wall?
            if (p.x <= border + radius) {
                p.x = border + radius;
                v.x *= -1;
            } else if (p.x >= width - border - radius) {
                p.x = width - border - radius;
                v.x *= -1;
            }
            if (p.y <= border + radius) {
                p.y = border + radius;
                v.y *= -1;
            } else if (p.y >= height - border - radius) {
                p.y = height - border - radius;
                v.y *= -1;
            }
            // collision with other stars
            for (Star b : others) {
                if (b == this) continue;
                PVector bToThis = new PVector(p.x - b.p.x, p.y - b.p.y);
                // collision?
                if (bToThis.mag() <= radius + b.radius) {
                    // set position
                    bToThis.setMag(radius + b.radius - 0.1f);
                    p.set(b.p.x + bToThis.x, b.p.y + bToThis.y);
                    // deflect velocity
                    PVector vel = new PVector(v.x, v.y);
                    vel.setHeading(bToThis.heading());
                    v = vel;
                }
            }
        }
    }
}