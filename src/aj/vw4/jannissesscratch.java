package aj.vw4;

import java.util.*;
import java.util.stream.IntStream;

class Scratch {
    public static void main(String[] args) {
        List<String> immutable = List.of("Tom", "Hansdanf", "Gian Heike", "Gent Hasangent", "Atur Freyer", "Achler", "Per", "Jannis");
        List<String> mutable = new ArrayList<>(immutable);


        //mutable.sort((s1, s2) -> s1.length() - s2.length());  // Nach länge
        mutable.sort(Comparator.comparingInt(String::length).thenComparing(String::compareTo));  // Nach länge danach alphabetisch
        //Collections.sort(mutable.stream().toList());        //Streams geben immer immutable Lists zurück
        //immutable.sort(String::compareTo);                  //Das geht natürlich nicht
        immutable = immutable.stream().sorted().toList();   //Das schon

        System.out.println("Mutabierbar: " + Arrays.asList(mutable.toArray()));
        System.out.println("Unmutierbar: " + Arrays.asList(immutable.toArray()));






        Set<List<Integer>> listSet = new HashSet<>(Set.of(
                IntStream.rangeClosed(0, 2).boxed().toList(),
                IntStream.rangeClosed(3, 6).boxed().toList(),
                IntStream.rangeClosed(7, 10).boxed().toList()
        ));

        System.out.println(listSet.stream().flatMap(Collection::stream).parallel().reduce(0, (x, y) -> {
            System.out.println(x + ", " + y);
            return x + y;
        }));

        System.out.println(powInCool(2, 4));
        IntStream.iterate(0, i -> i-=-1).forEach(System.out::println);
    }

    static int powInCool(int value, int power) {
        return IntStream.range(0, power).reduce(1, (x, y) -> x * value);
    }
}