package chapter6.item39.Test;
// Program to process marker annotations
import java.lang.reflect.*;
public class RunTests {
    static int tests, passed;
    public static void main(String[] args) throws Exception {
        // 정규화된 클래스 이름을 받는다.
//        Class<?> testClass = Class.forName("chapter6.item39.Test.SampleTest");
//        Class<?> testClass = Class.forName("chapter6.item39.Test.SampleWithParams");
        Class<?> testClass = Class.forName("chapter6.item39.Test.SampleWithRepeatable");
        // 그리고 해당 클래스의 메서드들을 순회한다.
        for (Method m : testClass.getDeclaredMethods()) {
            // 메서드 중, Test 애너테이션이 달린 메서드만 호출한다.
            // SampleTestRun(m);
            // ExceptionTestRun(m);
            // ExceptionTestsRun(m);
            ExceptionWithRepeatableRun(m);
        }
        System.out.printf("Passed: %d, Failed: %d%n",
                passed, tests - passed);
    }
    public static void SampleTestRun(Method m) {
        // @Test 를 사용한 예시
        if (m.isAnnotationPresent(Test.class)) {
            try {
                // 해당 메서드를 실행한다.
                m.invoke(null);
            } catch (InvocationTargetException wrappedExc) {
                // 만약 해당 메서드를 실행할 때, InvocationTargetException이 발생했다면
                // @Test 애너테이션을 잘못 사용했다는 뜻이다.
                Throwable exc = wrappedExc.getCause();
                System.out.println(m + " failed: " + exc);
            } catch (Exception exc) {
                System.out.println("Invalid @Test: " + m);
            }
        }
    }
    public static void ExceptionTestRun(Method m) {
        // @ExceptionTest 를 사용한 예시
        if (m.isAnnotationPresent(ExceptionTest.class)) {
            tests++;
            try {
                // 해당 메서드를 실행한다.
                m.invoke(null);
                // 만약 해당 메서드를 실행할 때, InvocationTargetException이
                // 발생하지 않았다면, 해당 테스트는 실패한 테스트이다.
                System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
            } catch (InvocationTargetException wrappedExc) {
                Throwable exc = wrappedExc.getCause();
                // ExceptionTest 애너테이션에 매개변수로 전달된 클래스를 꺼낸다.
                Class<? extends Throwable> excType =
                        m.getAnnotation(ExceptionTest.class).value();
                if (excType.isInstance(exc)) {
                    // 만약 해당 클래스가 ExceptionTest의 인스턴스라면 통과한다.
                    passed++;
                } else {
                    // 그렇지 않다면 테스트는 실패한 테스트이다.
                    System.out.printf(
                            "Test %s failed: expected %s, got %s%n",
                            m, excType.getName(), exc);
                }
            } catch (Exception exc) {
                System.out.println("Invalid @Test: " + m);
            }
        }
    }
    public static void ExceptionTestsRun(Method m) {
        // @ExceptionTests 를 사용한 예시
        if (m.isAnnotationPresent(ExceptionTest.class)) {
            tests++;
            try {
                m.invoke(null);
                System.out.printf("Test %s failed: no exception%n", m);
            } catch (Throwable wrappedExc) {
                Throwable exc = wrappedExc.getCause();
                int oldPassed = passed;
                // ExceptionTests 애너테이션에 매개변수로 전달된 클래스 배열을 꺼낸다.
                Class<? extends Exception>[] excTypes =
                        m.getAnnotation(ExceptionTests.class).value();
                for (Class<? extends Exception> excType : excTypes) {
                    // 배열의 각 원소를 순회한다.
                    if (excType.isInstance(exc)) {
                        // 배열의 원소 중 하나와 현재 발생한 예외인 exc의 클래스가 일치하면
                        // 해당 테스트는 성공한 것이다.
                        passed++;
                        break;
                    }}
                if (passed == oldPassed)
                    System.out.printf("Test %s failed: %s %n", m, exc);
            }
        }
    }
    public static void ExceptionWithRepeatableRun(Method m) {
        // @Repeatable 을 사용한 애너테이션과 그의 컨테이너 애너테이션을 사용하는 예제
        if (m.isAnnotationPresent(ExceptionWithRepeatable.class)
                || m.isAnnotationPresent(ExceptionWithRepeatableContainer.class)) {
            // ExceptionWithRepeatable 또는 그의 컨테이너 애너테이션인지 함께 검사
            tests++;
            try {
                m.invoke(null);
                System.out.printf("Test %s failed: no exception%n", m);
            } catch (Throwable wrappedExc) {
                Throwable exc = wrappedExc.getCause();
                int oldPassed = passed;
                ExceptionWithRepeatable[] excTests =
                        m.getAnnotationsByType(ExceptionWithRepeatable.class);
                for (ExceptionWithRepeatable excTest : excTests) {
                    if (excTest.value().isInstance(exc)) {
                        passed++;
                        break;
                    }}
                if (passed == oldPassed)
                    System.out.printf("Test %s failed: %s %n", m, exc);
            }}
    }
}