package chapter6.item39.Test;

public class SampleTest {
    // 테스트에 사용할 메서드들이 존재하는 클래스
    // 7개의 정적 메서드가 있다.
    // 그리고 8개의 메서드 중 4개의 메서드(m1, m3, m5, m7)에
    // @Test 애너테이션을 달았다. ( 나머지 4개의 메서드는 테스트 도구가 무시한다 )
    // 또한, m3, m7은 예외를 던지고 m1, m5는 그렇지 않다.
    // m5는 @Test 애너테이션을 잘못 사용했다. (정적 메서드가 아님)
    @Test public static void m1() { } // Test should pass
    public static void m2() { }
    @Test public static void m3() { // Test should fail
        throw new RuntimeException("Boom");
    }
    public static void m4() { }
    @Test public void m5() { } // INVALID USE: nonstatic method
    public static void m6() { }
    @Test public static void m7() { // Test should fail
        throw new RuntimeException("Crash");
    }
    public static void m8() { }
}
