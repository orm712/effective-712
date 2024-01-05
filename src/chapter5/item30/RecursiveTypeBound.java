package chapter5.item30;

import java.util.*;

public class RecursiveTypeBound {
    //모든 타입 E는 자신과 비교할 수 있다.
    //Comparable<E> 인터페이스를 구현하는 모든 타입 E는 자신과 비교할 수 있다.(E는 Comparable 인터페이스를 구현해야 한다.)
    //Comparable이 있기 때문에 상호 비교 가능하다.
    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty())
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");

        E result = null;
        for (E e : c)
            if (result == null || e.compareTo(result) > 0)
                result = Objects.requireNonNull(e);

        return result;
    }

    public static void main(String[] args) {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println(max(integerList));
    }
}