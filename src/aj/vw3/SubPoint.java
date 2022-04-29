package aj.vw3;

import java.util.Arrays;

public class SubPoint extends Point {

    SubPoint(double x, double y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    public static void main(String[] args) {
        Point p = new Point(0,1);
        SubPoint s = new SubPoint(0, 1);
        System.out.println(p.equals(s));
        Arrays.stream(s.getClass().getClasses()).anyMatch(c -> c == p.getClass());
        System.out.println(s.equals(p));
        System.out.println(new SubPoint(1,2).equals(new SubPoint(1,2)));
    }
}
