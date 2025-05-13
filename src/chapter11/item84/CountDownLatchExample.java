package chapter11.item84;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CountDownLatchExample {
    public static void main(String[] args) throws Exception{
        long javaCDLTime = timeCheckWithJavaCDL(Executors.newFixedThreadPool(1000), 1000, () -> {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        });
        long slowCDLTime = timeCheckWithSlowCDL(Executors.newFixedThreadPool(1000), 1000, () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        System.out.println("Java CountDownLatch 소요시간: " + javaCDLTime);
        System.out.println("Slow CountDownLatch 소요시간: " + slowCDLTime);
    }
    // 동시 실행 시간을 재는 예시 프레임워크
    public static long timeCheckWithJavaCDL(Executor executor, int concurrency, Runnable action) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                // 타이머에게 준비가 되었음을 알림
                ready.countDown();
                try {
                    start.await(); // 모든 Worker 스레드가 준비될 때 까지 기다림
                    action.run();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().isInterrupted());
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown(); // 타이머에게 작업을 마쳤음을 알림
                }
            });
        }
        ready.await(); // 모든 Worker가 준비될 때까지 기다림
        long startNanos = System.nanoTime();
        // Worker들을 깨움 (즉, start.await() 중이던 스레드들이 다음 작업을 수행할 수 있도록 함)
        start.countDown();
        // 모든 Worker들이 일을 끝마칠때까지 대기 (즉, 모든 스레드들이 작업을 끝내고 done.countDown()을 호출하기를 기다림)
        done.await();
        return System.nanoTime() - startNanos;
    }
    public static long timeCheckWithSlowCDL(Executor executor, int concurrency, Runnable action) {
        SlowCountDownLatch ready = new SlowCountDownLatch(concurrency);
        SlowCountDownLatch start = new SlowCountDownLatch(1);
        SlowCountDownLatch done = new SlowCountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                // 타이머에게 준비가 되었음을 알림
                ready.countDown();
                try {
                    start.await(); // 모든 Worker 스레드가 준비될 때 까지 기다림
                    action.run();
                } finally {
                    done.countDown(); // 타이머에게 작업을 마쳤음을 알림
                }
            });
        }
        ready.await(); // 모든 Worker가 준비될 때까지 기다림
        long startNanos = System.nanoTime();
        // Worker들을 깨움 (즉, start.await() 중이던 스레드들이 다음 작업을 수행할 수 있도록 함)
        start.countDown();
        // 모든 Worker들이 일을 끝마칠때까지 대기 (즉, 모든 스레드들이 작업을 끝내고 done.countDown()을 호출하기를 기다림)
        done.await();
        return System.nanoTime() - startNanos;
    }
    static class SlowCountDownLatch {
        private int count;

        public SlowCountDownLatch(int count) {
            if (count < 0)
                throw new IllegalArgumentException(count + " < 0");
            this.count = count;
        }
        public void await() {
            while (true) {
                synchronized(this) {
                    if (count == 0)
                        return;
                }
            }
        }
        public synchronized void countDown() {
            if (count != 0)
                count--;
        }
    }
}
