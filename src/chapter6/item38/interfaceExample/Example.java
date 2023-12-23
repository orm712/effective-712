package chapter6.item38.interfaceExample;

import java.util.Arrays;
import java.util.Collection;

public class Example {
    public static void main(String[] args) {
        // Operation을 인수로 받는 함수에
        // Operation을 implement한 Enum 타입이면 무엇이든 넣을 수 있다.
        doSomething(BasicOperation.PLUS, 10.0, 20.0);
        doSomething(ExtendedOperation.EXP, 10.0, 20.0);

        // class 리터럴을 '한정적 타입 토큰' 처럼 사용한다.
        // 즉, class 리터럴을 제공해 런타임 제네릭 타입 정보를 제공한다.
        test(ExtendedOperation.class, 10.0, 5.0);
        System.out.println();
        test(BasicOperation.class, 10.0, 5.0);
        System.out.println();

        // 한정적 와일드카드 타입을 넘긴다.
        test(Arrays.asList(ExtendedOperation.values()), 10.0, 5.0);
    }
    // Operation을 인수로 받는 함수
    public static void doSomething(Operation op, double x, double y) {
        System.out.println(op.apply(x, y));
    }

    // 1. `Class 객체` 를 넘기는 방법
    // <T extends Enum<T> & Operation> 가 복잡하게 느껴 지는데,
    // 이는 Class<T> 객체가 enum 타입이면서, Operation의 하위 타입이어야 한다는 뜻이다.
    // enum 타입이어야 상수 원소들을 순회할 수 있고,
    // Operation의 하위 타입이어야 apply를 통해 연산을 수행할 수 있기 때문이다.
    public static <T extends Enum<T> & Operation> void test(
            Class<T> opEnumType, double x, double y) {
        for(Operation op : opEnumType.getEnumConstants()) {
            // 해당 enum 타입의 각 상수들을 순회하며 연산한 결과 출력
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
        }
    }

    // 2. `한정적 와일드카드 타입`인 `Collection<? extends Operation>`을 넘기는 방법
    public static void test(Collection<? extends Operation> opSet,
                            double x, double y) {
        for(Operation op : opSet) {
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
        }
    }
}