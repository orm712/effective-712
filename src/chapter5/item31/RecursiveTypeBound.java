package chapter5.item31;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

// 와일드카드 타입을 사용해 재귀적 타입 한정을 다듬었다. (187쪽)
public class RecursiveTypeBound {
//    public static <E extends Comparable<E>> E max(Collection<E> c) {
//        if (c.isEmpty())
//            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
//
//        E result = null;
//        for (E e : c)
//            if (result == null || e.compareTo(result) > 0)
//                result = Objects.requireNonNull(e);
//
//        return result;
//    }

    //PECS를 두 번 적용
    //입력 매개변수에서는 E 인스턴스를 생산
    //타입 매개변수에서는 Comparable<E> E 인스턴스를 소비
    //Comparable은 언제나 소비자 -> 일반 적으로 Comparable<? super E>를 사용
    //Comparator도 매한가지
    public static <E extends Comparable<? super E>> E max(List<? extends E> list) {
        if (list.isEmpty())
            throw new IllegalArgumentException("빈 리스트");

        E result = null;
        for (E e : list)
            if (result == null || e.compareTo(result) > 0)
                result = e;

        return result;
    }

    public static void main(String[] args) {
//        List<String> argList = Arrays.asList(args);

        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        max(scheduledFutures);
        //왜 이렇게까지 해야 하나?
        //ScheduledFuture는 Comparable<ScheduledFuture>를 구현하지 않음


//        System.out.println(max(argList));
    }
}