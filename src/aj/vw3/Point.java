package aj.vw3;

class Point {
    private double x, y;
    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object other) { 1
        if (other == null) return false; // Null abwehren!
        if (other == this) return true; // Bin ich's selbst?
        if (other.getClass() != getClass()) return false; // Andere Klasse?
        Point that = (Point)other; // Casting
        return this.x == that.x && this.y == that.y; // Was definiert Gleichheit?
    }
}

class subPoint extends Point {

    subPoint(double x, double y) {
        super(x, y);
    }
}

public class scratch {
    public static void main(String[] args) {
        Point point = new Point();
    }
}