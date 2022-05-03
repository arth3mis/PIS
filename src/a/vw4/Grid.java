package a.vw4;

import java.util.Arrays;

import static processing.core.PApplet.*;

public class Grid {

    final int L;
    final int A;
    private int[] data;

    Grid(int l) {
        L = l;
        A = l*l;
        data = new int[A];
    }

    Grid(Grid g) {
        L = g.L;
        A = g.A;
        data = Arrays.copyOf(g.data, A);
    }

    /**
     * emulates 2d CCW rotation in grid
     */
    void rotate() {
        int[] temp_grid = new int[A];
        for (int i=0; i<A; i++) {
            temp_grid[i+L*(L-1)-(i%L)*(L+1)-(i/L)*(L-1)] = data[i];
        }
        arrayCopy(temp_grid, data);
    }

    /**
     * rotates repeatedly (CCW)
     */
    void rotate(int n) {
        for (int i=1; i<=(n%4); i++) { rotate(); }
    }

    /**
     * shifts tiles as far left as possible, merges adjacent equals, repeats shifting.
     * Returns score achieved with this move
     */
    int move() {
        int score = 0;
        shift();
        score = merge();
        shift();
        return score;
    }

    /**
     * checks (per row, l-to-r) free space to the left of tiles, moves them there
     */
    void shift() {
        int offset=0;
        for (int i=0; i<A; i++) {
            if (i%L == 0) {
                offset = 0;
            }
            if (data[i] == 0) {
                offset++;
            } else if (offset > 0) {
                data[i-offset] = data[i];
                data[i] = 0;
            }
        }
    }

    /**
     * checks (per row, l-to-r) adjacent values and if they're equal,
     * adds right value to left field and deletes the former.
     */
    int merge() {
        int score = 0;
        for (int i=0; i<A; i++) {
            if (i%L < L-1) {
                if (data[i] > 0 && data[i] == data[i+1]) {
                    data[i] += data[i+1];
                    data[i+1] = 0;
                    score += data[i];
                }
            }
        }
        return score;
    }

    /**
     * counts spaces without tiles in grid
     */
    int free_slots() {
        int i = 0;
        for (int val : data) {
            if (val == 0) i++;
        }
        return i;
    }

    /**
     * sets a value at the n_th free slot in the grid
     */
    void insert_tile(int n, int val) {
        for (int i=0; i<A; i++) {
            if (data[i] == 0) {
                if (n == 0) {
                    data[i] = val;
                    break;
                }
                n--;
            }
        }
    }

    /**
     * inserts a tile (value 2 or 4) into the grid in a random, free slot
     */
    void random_tile() {
        int pos, val;
        pos = (int)random(0, free_slots());
        val = random(0, 1) < 0.9 ? 2 : 4;
        insert_tile(pos, val);
    }

    int[] copyData() {
        return Arrays.copyOf(data, A);
    }

    Grid copy() {
        return new Grid(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != getClass()) return false;
        Grid that = (Grid)other;
        return Arrays.equals(that.data, data);
    }

    @Override
    public String toString() {
        String s = "\n";
        for (int i=0; i<A; i++) {
            if (i!=0 && i%L == 0) s += "\n" + "------|".repeat(L) + "\n";
            s += String.format("%5s |", data[i] > 0 ? data[i]+"" : "");
        }
        return s;
    }

    // missing helper methods (Processing syntax)
    static float random(float low, float high) {
        return new java.util.Random().nextFloat(low, high);
    }
}
