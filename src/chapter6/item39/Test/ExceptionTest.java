package chapter6.item39.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    // 이 애너테이션의 매개변수 타입은 `Class<? extends Throwable>` 이다.
    // Throwable을 extend한 클래스 객체, 즉 모든 Exception과 Error 타입을 수용한다.
    Class<? extends Throwable> value();
}
