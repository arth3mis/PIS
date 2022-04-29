package aj.vw3;

class Point {
    private double x, y;
    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object other) {
        if (other == null) return false; // Null abwehren!
        if (other == this) return true; // Bin ich's selbst?
        if (other.getClass() != getClass()) return false; // Andere Klasse?
        Point that = (Point)other; // Casting
        return this.x == that.x && this.y == that.y; // Was definiert Gleichheit?
    }
}
