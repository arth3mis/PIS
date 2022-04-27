package herzi.nim;

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
    
    public String toString() {
        return "(" + row + ", " + number + ")";
    }
}