package chapter4.item19;

import java.time.Instant;

public class Parent {

    public Parent() {
        // 오동작의 원인 - 상위 클래스에서 재정의 메서드 호출
        overrideMe();
    }

    public void overrideMe() {
        System.out.println("부모 override 메서드 호출");
    }
}

final class Child extends Parent {

    // 생성자에서 초기화
    private final Instant instant;

    Child() {
        instant = Instant.now();
    }

    // 재정의 가능 메서드
    @Override
    public void overrideMe() {
        System.out.println(instant);
    }

    public static void main(String[] args) {
        Child child = new Child();
        child.overrideMe();
    }
}