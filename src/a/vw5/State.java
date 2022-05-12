package a.vw5;

import java.util.Arrays;
import java.util.stream.IntStream;

public class State {
    int hash;
    State(int[] r, int bitsPerRow) {
        int[] l = Arrays.stream(r).sorted().toArray();
        hash = IntStream.range(0, r.length)
                .map(i -> l[i] << bitsPerRow * i)
                .reduce((a,b) -> a | b).orElse(0);
    }

    int get() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        return hash == ((State) obj).hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
