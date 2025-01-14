package chapter3.item10;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ContainsExample {
    public static void main(String[] args) {
        Set<Point> set = Set.of(
                new Point( 1, 0), new Point( 0, 1),
                new Point(-1, 0), new Point( 0, -1));
        System.out.println(set.contains(new CounterPoint(1, 0))); // false
    }
    static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        // 리스코프 치환 원칙 위배!
        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass())
                return false;
            Point p = (Point) o;
            return p.x == x && p.y == y;
        }
    }
    static class CounterPoint extends Point {
        private static final AtomicInteger counter =
                new AtomicInteger();
        public CounterPoint(int x, int y) {
            super(x, y);
            counter.incrementAndGet();
        } public static int numberCreated() {
            return counter.get(); }
    }
}
