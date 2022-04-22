package j;

import aj_vw1.ArtSketch;
import herzi.nim.Nim;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class vw02_Nim extends PApplet {

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new vw02_Nim());
    }

    Nim nim;

    public void settings() {
        size(900, 700);
        startNewGame();
    }

    public void setup() {
    }

    void startNewGame() {
        int rowCount = (int) random(2, 5);
        int columnCount = (int) random(3, 7);
        nim = Nim.of(Nim.randomSetup(IntStream.generate(() -> columnCount).limit(rowCount).toArray()));
        System.out.println(nim);
    }

    public void draw() {

    }


    class Stick {
        boolean active;
        int row;
        int col = color(random(80,150), random(50, 90), random(50,100));
        PVector pos;
        PVector vel;
        float rot;

        public Stick(boolean active, int row, PVector pos) {
            this.active = active;
            this.row = row;
            this.pos = pos;
        }

        public void draw() {

        }
    }

}
