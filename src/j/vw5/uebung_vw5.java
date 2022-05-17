package j.vw5;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Scratch {
    public static void main(String[] args) {
        List<Set<Integer>> listOfSetOfNumbers = List.of(Set.of(2, 5, 6), Set.of(1, 4, 5, 8));

        // Functional Interfaces
        List<List<Integer>> l = List.of(List.of(42));

        // Predicate
        l.removeIf(
                list -> list.size() >= 3
        );

        // Function<Inputtyp, Outputtyp>
        l.stream().map(
                list -> list.size() // List::size
        );

        // Supplier
        l = Stream.generate(
                () -> List.of(1, 2, 3)
        ).toList();

        // Consumer
        l.forEach(
                list -> list.forEach(System.out::println) //System.out::println
        );


    }

    Set<Integer> level(Set<Integer> integers) {
        return integers.stream().map(i -> i % 2 == 0 ? i / 2 : i * 2).collect(Collectors.toSet());
    }

}

class Rectangle {
    float width, height;

    public Rectangle(float width, float heigth) {
        this.width = width;
        this.height = heigth;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;                            // Not null
        try {
            if (other.equals(null)) throw new StackOverflowError(); // Am I
        } catch (StackOverflowError e) {
            return false;
        }
        if (!(other instanceof Rectangle that)) return false;       // Klassenvergleich
        return width == that.width && height == that.height;        // gleichheit
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}