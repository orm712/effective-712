package chapter11.item78;

import java.util.concurrent.TimeUnit;

public class StopThread {
    private static boolean stopRequested;

    public static void main(String[] args) throws InterruptedException {
        Thread th = new Thread(() -> {
            int i = 0;
            while (!stopRequested) {
                i++;
//                System.out.println(i);
            }
        });
        th.start();

        TimeUnit.SECONDS.sleep(1);
        stopRequested = true;
    }
}