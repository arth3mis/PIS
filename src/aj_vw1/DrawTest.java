package aj_vw1;

import processing.core.PApplet;

public class DrawTest extends PApplet {

    float width = 10;
    float speedResistance = 42;

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new DrawTest());
    }

    public void settings() {
        size(600,600);
    }

    public void setup() {
        background(0);
        stroke(color(200, 150, 168));
    }

    public void draw() {
        if (mousePressed) {
            strokeWeight((speedResistance * width) / (speedResistance + dist(pmouseX, pmouseY, mouseX, mouseY)));
            line(pmouseX, pmouseY, mouseX, mouseY);
        } else {
            System.out.println("Hi");
        }
    }
}
