package chapter6.item39.Test;

import java.lang.annotation.*;
/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수가 없는 정적 메서드에서만 쓸 수 있다.
 */
// @Test가 런타임에도 유지되어야 한다는 표시이다.
// 이를 생략하면, 테스트 도구는 @Test를 인식할 수 없다.
@Retention(RetentionPolicy.RUNTIME)
// @Test가 메서드 선언에서만 사용되어야 한다는 표시이다.
// 따라서, @Test는 클래스 선언, 필드 선언 등의 요소에는 달 수 없다.
@Target(ElementType.METHOD)
public @interface Test {
}