package chapter9.item61;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BoxingReflectionTest {
    static class TestClass {
        public static void TT(int v, double t) {
            System.out.println(v + t);
        }
    }
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<TestClass> t = (Class<TestClass>) Class.forName("chapter9.item61.BoxingReflectionTest$TestClass");
        Method method = t.getMethod("TT", Integer.TYPE, Double.TYPE);
        // Method.invoke(Object obj, Object... args)는 객체들만 인자로 받지만,
        // 기본 타입 값들을 넘겼음에도 오토 박싱이 이루어져 정상적으로 컴파일 및 호출된다.
        method.invoke(t, 10, 20);
    }
}
