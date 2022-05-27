package a.vw8;

import java.util.Objects;

public class Move {
    public final int row, number;

    public static Move of(int row, int number) {
        return new Move(row, number);
    }

    private Move(int row, int number) {
        if (row < 0 || number < 1) throw new IllegalArgumentException();
        this.row = row;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return row == move.row && number == move.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, number);
    }

    public String toString() {
        return "(" + row + ", " + number + ")";
    }
}