package chapter5.item31;

import java.util.*;

// 코드 30-2의 제네릭 union 메서드에 와일드카드 타입을 적용해 유연성을 높였다. (185-186쪽)
public class Union {
    public static <E> Set<E> union(Set<? extends E> s1,
                                   Set<? extends E> s2) {
        //s1과 s2 모두 E의 생산자
        //그렇기 때문에 Set<Integer>와 Set<Double>을 합칠 수 있다.
        Set<E> result = new HashSet<E>(s1);
        result.addAll(s2);
        return result;
    }

    // 향상된 유연성을 확인해주는 맛보기 프로그램 (185쪽)
    public static void main(String[] args) {
        Set<Integer> integers = new HashSet<>();
        integers.add(1);
        integers.add(3);
        integers.add(5);

        Set<Double> doubles =  new HashSet<>();
        doubles.add(2.0);
        doubles.add(4.0);
        doubles.add(6.0);

        //클라이언트는 와일드카드 타입이 쓰였다는 사실 모름
        Set<Number> numbers = union(integers, doubles);

//      // 코드 31-6 자바 7까지는 명시적 타입 인수를 사용해야 한다. (186쪽)
//      Set<Number> numbers = Union.<Number>union(integers, doubles);

        System.out.println(numbers);
    }
}