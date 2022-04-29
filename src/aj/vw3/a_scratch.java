package aj.vw3;

class BottleScratch {
    public static void main(String[] args) {
        Bottle a = new Bottle(25, 3, Material.PLASTIC, Content.JUICE);
        Bottle b = new Bottle(25, 3, Material.PLASTIC, Content.WATER);
        System.out.println(a.equals(b));
    }
}

class Bottle {
    double height, radius;
    double volume, fill;
    Material material;
    Content content;

    Bottle(double h, double r) {
        this(h, r, null, null);
    }

    Bottle(double h, double r, Material m, Content c) {
        height = h;
        radius = r;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != this.getClass()) return false;
        Bottle that = (Bottle) other;
        return that.height == height
                && that.radius == radius
                && that.material == material;
                //&& that.content == content;
    }
}

enum Content {
    WATER, JUICE, SODA, BEER
}

enum Material {

    PLASTIC(0.25), GLASS(0.08), TIN(0.15);

    final double PFAND;
    int color;

    Material(double pfand) {
        PFAND = pfand;
    }

    void setColor(int color) {
        this.color = color;
    }
}