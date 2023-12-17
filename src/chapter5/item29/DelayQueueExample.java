package chapter5.item29;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueueExample {

    // Delayed 인터페이스를 구현하는 클래스
    static class DelayedElement implements Delayed {
        private final long delayTime; // 지연 시간
        private final long createTime; // 생성 시간

        DelayedElement(long delayInMilliseconds) {
            this.delayTime = delayInMilliseconds;
            this.createTime = System.currentTimeMillis();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = createTime + delayTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
                return -1;
            }
            if (this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
                return 1;
            }
            return 0;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<DelayedElement> queue = new DelayQueue<>();

        // DelayQueue에 요소 추가
        queue.put(new DelayedElement(5000)); // 5초 지연

        // 요소를 꺼내려고 시도
        System.out.println("Waiting for the element...");
        DelayedElement element = queue.take(); // 요소가 준비될 때까지 블록

        System.out.println("Element retrieved from the queue");
    }
}
