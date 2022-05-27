package aj.vw6;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;
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
        for (int i = 0; i < 100; i++) {
//            System.out.println(i + ": " + isPrime.test(i));
        }

        // RSA
        // todo: maybe add rule that e != p||q, d != e
        boolean log = true;
        int bitN = 2048, bitDiff = 10;
        bitN = 32;
        bitDiff = 4;
        BigInteger p = new BigInteger(bitN/2 - bitDiff/2, 1, new Random());
        BigInteger q = new BigInteger(bitN/2 + bitDiff/2, 1, new Random());
        // alternative (maybe faster, maybe less consistent)
//        BigInteger p0 = BigInteger.probablePrime(bitN/2 - bitDiff/2, new Random());
//        p = new BigInteger("7");
//        q = new BigInteger("53");
        BigInteger n = p.multiply(q);

        if (log) {
            System.out.println("p = " + p);
            System.out.println("q = " + q);
            System.out.println("n = " + n);
            System.out.println("bitlength of n = " + n.bitLength());
        }

        BigInteger p_1 = p.subtract(BigInteger.ONE);
        BigInteger q_1 = q.subtract(BigInteger.ONE);
        BigInteger ln = p_1.multiply(q_1).abs().divide(p_1.gcd(q_1));

        if (log) System.out.println("λ(n) = " + ln);

        BigInteger e = BigInteger.TWO.pow(16).add(BigInteger.ONE);
        e = BigInteger.probablePrime(Math.min(q.bitLength()+1, 18), new Random());
        while (!e.isProbablePrime(1) || e.compareTo(ln) >= 0 || !e.gcd(ln).equals(BigInteger.ONE)) {
            if (e.compareTo(ln) >= 0) e = BigInteger.TWO;
            e = e.nextProbablePrime();
        }

        if (log) System.out.println("e = " + e);

        BigInteger d = e.modInverse(ln);

        if (log) System.out.println("d = " + d);

        BigInteger test = new BigInteger("187");
        BigInteger c = test.modPow(e, n);
        BigInteger dec = c.modPow(d, n);

        // storing c
        String test64 = Base64.getEncoder().withoutPadding().encodeToString(Integer.valueOf(Integer.MAX_VALUE).toString().getBytes());
        test64 = c.toString(Character.MAX_RADIX);
        System.out.println(test64);
        System.out.println(new BigInteger(test64, Character.MAX_RADIX));
//        System.out.println(new String(Base64.getDecoder().decode(test64)));

        System.out.println(test+" -> "+c+" / "+c.toString(36)+" -> "+dec);

        boolean check = true;
        for (int i = 0; i < 1000; i++) {
            BigInteger x = new BigInteger(new Random().nextInt(4, 1024), new Random());
            BigInteger y = x.modPow(e, n);
            BigInteger z = y.modPow(d, n);
            if (!x.equals(z)) {
                check = false;
            }
        }
        System.out.println(check);
    }
}
