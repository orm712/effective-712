package chapter3.item10;

import java.awt.*;
import java.util.Objects;

public class CompositionExample {
    static class ColorPoint {
        private final Point point;
        private final Color color;
        public ColorPoint(int x, int y, Color color) {
            point = new Point(x, y);
            this.color = Objects.requireNonNull(color);
        }
        /**
         * ColorPoint의 Point 뷰를 반환
         */
        public Point asPoint() {
            return point;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ColorPoint))
                return false;
            ColorPoint cp = (ColorPoint) o;
            return cp.point.equals(point) && cp.color.equals(color);
        }
    }
    public static void main(String[] args) {
        Point p = new Point(1, 2);
        ColorPoint cp = new ColorPoint(1, 2,  Color.BLACK);

        System.out.println(p.equals(cp.asPoint()));
    }
}