package stuff;

import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InfiniteStreams {
    public static void main(String[] args) {
//      Erzeugen Sie einen unendlichen Stream von positiven geraden Zahlen, der bei 0 beginnt.
        IntStream.iterate(0, n -> n+2);
//      Erzeugen Sie einen unendlichen Stream von positiven ungeraden Zahlen, der bei 1 beginnt.
        IntStream.iterate(1, n -> n+2);
//      Erzeugen Sie einen unendlichen Stream von negativen geraden Zahlen, der bei 0 beginnt.
        IntStream.iterate(0, n -> n-2);
//      Erzeugen Sie einen unendlichen Stream von negativen ungeraden Zahlen, der bei -1 beginnt.
        IntStream.iterate(-1, n -> n-2);
//      Erzeugen Sie einen unendlichen Stream, der die Fibonacci-Folge (0, 1, 1, 2, 3, 5, 8, 13, …) abbildet.
        Stream.iterate(new long[]{0, 1}, a -> new long[]{a[1], a[0]+a[1]});//.limit(8).forEach(a -> System.out.println(a[0]));
        Stream.iterate(new BigInteger[]{ BigInteger.ZERO, BigInteger.ONE }, p->new BigInteger[]{ p[1], p[0].add(p[1]) });
//      Erzeugen Sie einen unendlichen Stream, der nur aus Primzahlen besteht.
//      Erzeugen Sie einen unendlichen Stream, der eine negative Version der Fibonacci-Folge (0, -1, -1, -2, -3, -5, -8, -13, …) abbildet.
//      Erzeugen Sie einen unendlichen Stream, der aus Ganzzahlen ohne Primzahlen besteht.
//      Erzeugen Sie einen unendlichen Stream, mit dem sich wiederholend das Alphabet in Großbuchstaben (A-Z) wie im nachfolgenden Beispiel ausgeben lässt.
    }
}
