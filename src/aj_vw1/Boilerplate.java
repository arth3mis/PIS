package aj_vw1;

import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;

public class Boilerplate extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Boilerplate());
    }

    @Override
    public void settings() {
        //fullScreen();
        size(800, 450);
        smooth(8);
    }

    // variables
    //


    @Override
    public void setup() {
        frameRate(60);
    }

    @Override
    public void draw() {

    }

    @Override
    public void mousePressed() {
        if (mouseButton == LEFT) {
            println("LMB");
        } else if (mouseButton == RIGHT) {
            println("RMB");
        } else {
            println("?MB");
        }
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
