package aj.vw6;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.function.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Ubg {
    public static void main(String[] args) {
        IntPredicate testEven = i -> i % 2 == 0;
        //(IntPredicate) n -> n % 2 == 0;
        IntUnaryOperator digitCount = i -> (i+"").length();
        IntSupplier rand = () -> new Random().nextInt(1, 101);

        int sum = IntStream.rangeClosed(1, 100).filter(n -> n%5>0).sum();
        long prod = LongStream.iterate(3, n -> n <= 30, n -> n+3).reduce(0, (x, y) -> x*y);
        int qsum = IntStream.rangeClosed(5, 15).reduce(0, (x,y) -> x + y*y);
        int qsum2 = IntStream.rangeClosed(5, 15).map(n -> n*n).sum();
        IntPredicate isPrime = n -> IntStream.rangeClosed(2, (int)Math.sqrt(n)).parallel().noneMatch(i -> n%i == 0);
        for (int i = 0; i < 1; i++) {
//            System.out.println(i + ": " + isPrime.test(i));
        BigInteger bi = BigInteger.probablePrime(1040, new Random());
        BigInteger bi2 = BigInteger.probablePrime(1024, new Random());
        System.out.print(bi.toString().length() + " ");
        System.out.println(bi2.toString().length());
            System.out.println(bi.multiply(bi2).toString().length());
        }
    }
}
