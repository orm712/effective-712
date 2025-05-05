package chapter11.item81;

import java.util.concurrent.*;

public class CountLatchExample {
    public static void main(String[] args) throws Exception{
        long t = time(Executors.newFixedThreadPool(10), 10, () -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        System.out.println("소요시간: " + t);
        System.out.println(System.currentTimeMillis());
        System.out.println(System.nanoTime());
    }
    // 동시 실행 시간을 재는 예시 프레임워크
    public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
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
}
