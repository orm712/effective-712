package chapter3.item10;

import java.awt.Color;

public class ColorPointExample {
    public static void main(String[] args) {
        Point p = new Point(10, 20);
        ColorPoint cp = new ColorPoint(10, 20, Color.RED);

        System.out.println(p.equals(cp)); // true
        System.out.println(cp.equals(p)); // false
    }
}

// x, y 좌표 값을 갖는 클래스 Point
class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            // 주어진 o가 Point 인스턴스가 아닌 경우 무조건 false
            return false;
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}

// Point 클래스에 색상(color)를 추가해 확장한 클래스 ColorPoint
class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
    // 대칭성 위배!
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return false;
        return super.equals(o) && ((ColorPoint) o).color == color;
    }
}