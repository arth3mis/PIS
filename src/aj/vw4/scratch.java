package aj.vw4;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Scratchy {
    public static void main(String[] args) {
        List<String> immutable = List.of("aaa", "c", "bb");
        List<String> mutable = new ArrayList<>(immutable);
        Collections.sort(mutable);
        mutable.sort(Comparator.comparingInt(String::length).thenComparing(String::compareTo));

        Set<List<Integer>> listSet = new HashSet<>(Set.of(
                IntStream.rangeClosed(1,3).boxed().toList(),
                IntStream.rangeClosed(4,6).boxed().toList(),
                IntStream.rangeClosed(7,10).boxed().toList()));

        int sum = listSet.stream()
                .flatMap(Collection::stream)
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n)
                .sum();

        // n <- n + 1     AUD
        // n -> n + 1     PIS
    }
}
class Node {

    short a, b;

    @Override
    public int hashCode() {
        return (a << 16) + b;
    }
}