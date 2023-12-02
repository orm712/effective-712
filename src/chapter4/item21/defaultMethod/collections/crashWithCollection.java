package chapter4.item21.defaultMethod.collections;

import java.util.ArrayList;

public class crashWithCollection {
    public static void main(String[] args) {
        // removeIf() 를 재정의하지 않은 4.4.2 버전의 SynchronizedCollection
        SynchronizedCollection442<Integer> c = new SynchronizedCollection442<>(new ArrayList<>());
        c.add(1);
        c.add(2);
        c.add(3);
        c.add(4);
        // removeIf() 를 호출하면, Collection 인터페이스의 디폴트 구현을 호출하게 되므로
        // 별도의 동기화 작업이 이루어지지 않아 멀티 스레드 환경에서 `ConcurrentModificationException`을 유발하거나 에러가 발생할 수 있다.
        c.removeIf((Integer i)-> i == 2);
        // removeIf() 를 재정의한 4.4.4 버전의 SynchronizedCollection
        SynchronizedCollection<Integer> c2 = new SynchronizedCollection<>(new ArrayList<>());
        c2.add(1);
        c2.add(2);
        c2.add(3);
        c2.add(4);
        c2.add(5);
        // removeIf() 를 호출하면, SynchronizedCollection가 재정의한 removeIf()를 호출하게 되므로
        // 동기화 작업이 이루어져 멀티 스레드 환경에서도 안전하다.
        c2.removeIf((Integer i) -> i == 2);
    }
}
