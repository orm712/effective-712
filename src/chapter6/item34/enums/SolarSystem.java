package chapter6.item34.enums;

public enum SolarSystem {
    MERCURY(3.302e+23, 2.439e6),
    VENUS (4.869e+24, 6.052e6),
    EARTH (5.975e+24, 6.378e6),
    MARS (6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN (5.685e+26, 6.027e7),
    URANUS (8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    // enum 타입들은 근본적으로 불변이므로 모든 필드는 final이어야 한다.
    private final double mass;          // 질량
    private final double radius;        // 반지름
    private final double surfaceGravity;// 표면중력

    // 중력 상수 (단위: m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    SolarSystem(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        this.surfaceGravity = G * mass / (radius * radius);
    }

    // 필드를 public 으로 선언해도 되지만, private 으로 두고 별도의 접근자를 제공하자.
    public double mass() { return mass;}
    public double radius() { return radius;}
    public double surfaceGravity() { return surfaceGravity;}
    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;
    }
}
class SolarSystemTest {
    public static void main(String[] args) {
        double earthWeight = 1000.0;
        double mass = earthWeight / SolarSystem.EARTH.surfaceGravity();
        for(SolarSystem s : SolarSystem.values()) {
            System.out.printf("%s에서 무게는 %f 이다.%n", s, s.surfaceWeight(mass));
        }
    }
}
