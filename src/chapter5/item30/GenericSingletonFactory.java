package chapter5.item30;

import java.util.function.UnaryOperator;

public class GenericSingletonFactory {
    //항등 람다 함수 -> 받아서 그대로 반환
    private static final UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        //제네릭 싱글턴 팩토리 -> 요청한 타입 매개 변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리
        //T가 어떤 타입이든 UnaryOperator<Object>는 UnaryOperator<T>가 아니기 때문에 비검사 형변환 경고가 발생
        //-> @SuppressWarnings("unchecked") 어노테이션을 달아 경고를 숨긴다.
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    public static void main(String[] args) {
        String[] strings = { "a", "ab", "abc" };
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strings)
            System.out.println(sameString.apply(s));

        Number[] numbers = { 1, 2.0, 3L };
        UnaryOperator<Number> sameNumber = identityFunction();
        for (Number n : numbers)
            System.out.println(sameNumber.apply(n));

        //identifyFunction으로 제네릭 싱글턴 반환
    }
}