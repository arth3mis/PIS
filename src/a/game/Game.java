package a.game;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.stream.IntStream;

public class Game extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Game());
    }

    @Override
    public void settings() {
        fullScreen();
    }

    // variables
    //
    PImage img = createImage(16, 16, ARGB);

    @Override
    public void setup() {
        background(50,30,250);
        IntStream.range(0, img.width).forEach(i -> img.set(i, 0, color(255)));
        IntStream.range(0, img.height).forEach(i -> img.set(0, i, color(255)));
    }

    @Override
    public void draw() {
        img.resize(5, img.height);
        //println(img.width, img.height);
        cursor(img);
    }
}
