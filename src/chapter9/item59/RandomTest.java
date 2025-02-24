package chapter9.item59;

import java.util.Random;

public class RandomTest {
    static Random rnd = new Random();

    public static void main(String[] args) {
        int n = 2 * (Integer.MAX_VALUE / 3); // 약 14억
        int low = 0;

        for (int i=0; i < 1_000_000; i++) {
            if (randomNumber(n) < n / 2) { // 약 7억
                low++;
            }
        }

        System.out.println("low = " + low);
    }

    public static int randomNumber(int n) {
        return Math.abs(rnd.nextInt()) % n;
    }

    public static int randomNumber2(int n) {
        return rnd.nextInt(n);
    }

}
