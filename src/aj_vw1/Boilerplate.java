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

    final int LMB = 0, RMB = 1, XMB = 2;
    boolean[] mousePressed = new boolean[3];
    Map<Integer, Boolean> mouseTriggered = new HashMap<>();

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
