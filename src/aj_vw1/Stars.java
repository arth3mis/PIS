package aj_vw1;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Stars extends PApplet {

    List<Star> stars = new ArrayList<>();
    int starCounter = 69;
    int time = 0;
    float mouseMass =  100f;

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Stars());
    }

    public void settings() {
        size(600,600);
    }

    public void setup() {
        strokeWeight(0);
        for (int i = 0; i < starCounter; i++) {
            stars.add(new Meteoroid());
        }

    }

    public void draw() {
        time++;
        background(10, 0, 15);

        /*if (mousePressed) {
            Meteoroid meteoroid = new Meteoroid(mouseX, mouseY);
            meteoroid.randomizeVelocity();
            stars.add(meteoroid);
        }*/

        stars.forEach(Star::draw);
    }

    class Star {
        float x, y;
        int color;

        int size;
        int shineRadius;
        float shineSpeed = 0.1f;
        float shineAmplitude = 5;

        public Star() {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
            randomInit();
        }

        public Star(float x, float y) {
            this.x = x;
            this.y = y;
            randomInit();
        }

        void randomInit() {

            color = color(random(210, 255), random(210, 240), random(210, 255));
            size = (int)random(2,4);
            shineRadius = (int)random(5, 22);
            shineSpeed = random(0.001f, 0.02f);
            shineAmplitude = random(2, 0.8f * shineRadius);
        }
        void draw() {
            int tempShineRadius = (int) (shineRadius + shineAmplitude * sin(shineSpeed*time));
            for (int i = 0; i < tempShineRadius; i++) {
                fill(red(color), green(color), blue(color),  90f/(tempShineRadius+1f));
                ellipse(x, y, size+i, size+i);
            }
            fill(color);
            ellipse(x, y, size, size);
        }
    }

    class Meteoroid extends Star {

        static float acceleration = 0.1f;
        static float deceleration = 0.982f;
        float speed = random(8, 20);
        float vx;
        float vy;

        public Meteoroid() {
            super();
        }

        public Meteoroid(float x, float y) {
            super(x, y);
        }

        void randomizeVelocity() {
            vx = random(-1 , 1) * speed;
            vy = random(-1 , 1) * speed;
        }

        @Override
        void draw() {

            if (keyPressed) {
                if (keyCode == LEFT || key == 'a') vx += (-speed - vx) * acceleration;
                if (keyCode == RIGHT || key == 'd') vx -= -(speed - vx) * acceleration;

                if (keyCode == UP || key == 'w') vy += (-speed - vy) * acceleration;
                if (keyCode == DOWN || key == 's') vy -= -(speed - vy) * acceleration;
            }

            if (mousePressed) {
                float distance = dist(x, y, mouseX, mouseY) + 1f;
                vx += mouseMass * (mouseX - x) / (distance * distance * speed);
                vy += mouseMass * (mouseY - y) / (distance * distance * speed);
            }

            x += vx;
            y += vy;

            // Bounce
            if (x - 0.5 * size < 0) {
                x = size - x;
                vx *= -1;
            }
            if (y - 0.5 * size < 0) {
                y = size - y;
                vy *= -1;
            }
            if (x + 0.5 * size > width) {
                x = 2 * width - size - x;
                vx *= -1;
            }
            if (y + 0.5 * size > height) {
                y = 2 * height - size - y;
                vy *= -1;
            }


            super.draw();

            vx *= deceleration;
            vy *= deceleration;
        }
    }
}
