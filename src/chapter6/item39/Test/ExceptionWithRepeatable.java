package chapter6.item39.Test;

import java.lang.annotation.*;

// 반복 가능한 애너테이션
// '컨테이너 애너테이션'의 배열의 원소로 들어갈 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionWithRepeatableContainer.class)
public @interface ExceptionWithRepeatable {
    Class<? extends Exception> value();
}
