package j;

import processing.core.PApplet;

import java.util.Arrays;

public class BouncingCircle extends PApplet {

    Ball[] balls = new Ball[10];
    int ballDiameter = 42;
    int ballSpeed = 12;
    float mouseMass = 10f;

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new BouncingCircle());
    }

    public void settings() {
        size(600,600);
    }

    public void setup() {

        boolean ballDistance;
        int tolerance = 69;

        do {
            if (--tolerance < 0) throw new RuntimeException("Zu viele Bälle");
            for (int i = 0; i < balls.length; i++) {
                balls[i] = new Ball(color(random(100, 255), random(100, 255), random(100, 255)));
            }

            ballDistance = true;

            test:
            for (int i = 0; i < balls.length; i++) {
                for (int j = i + 1; j < balls.length; j++) {
                    if (dist(balls[i].x, balls[i].y, balls[j].x, balls[j].y) < ballDiameter) {
                        ballDistance = false;
                        break test;
                    }
                }
            }

        } while (!ballDistance);
    }

    public void draw() {
        background(0);

        Arrays.stream(balls).forEach(Ball::move);

        for (int i = 0; i < balls.length; i++) {
            for (int j = i + 1; j < balls.length; j++) {
                if (dist(balls[i].x, balls[i].y, balls[j].x, balls[j].y) < ballDiameter) {
                    double vx = balls[i].vx;
                    double vy = balls[i].vy;

                    balls[i].vx = balls[j].vx;
                    balls[i].vy = balls[j].vy;

                    balls[j].vx = vx;
                    balls[j].vy = vy;
                }
            }
        }


        Arrays.stream(balls).forEach(Ball::draw);
    }


    class Ball {

        public int x, y;
        public double vx, vy;
        public int color;
        Ball(int color) {
            this.color = color;
            randomPos();
        }
        void randomPos() {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
            vx = ballSpeed*(Math.random()-0.5);
            vy = ballSpeed*(Math.random()-0.5);
        }
        void draw() {
            fill(color);
            ellipse(x,y, ballDiameter, ballDiameter);
        }
        void move() {

            if (mousePressed) {
                float distance = dist(x, y, mouseX, mouseY);
                vx += mouseMass * (mouseX - x) / (distance * distance);
                vy += mouseMass * (mouseY - y) / (distance * distance);
            }

            x += vx;
            y += vy;

            if (x - 0.5 * ballDiameter < 0) {
                x = ballDiameter - x;
                vx *= -1;
            }
            if (y - 0.5 * ballDiameter < 0) {
                y = ballDiameter - y;
                vy *= -1;
            }
            if (x + 0.5 * ballDiameter > width) {
                x = 2 * width - ballDiameter - x;
                vx *= -1;
            }
            if (y + 0.5 * ballDiameter > height) {
                y = 2 * height - ballDiameter - y;
                vy *= -1;
            }
        }
    }


}
