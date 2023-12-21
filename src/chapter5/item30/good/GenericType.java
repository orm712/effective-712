package chapter5.item30.good;

import java.util.HashSet;
import java.util.Set;

public class GenericType {
    //제네릭 타입이나 제네릭 메서드나 적용하는 방식은 똑같다.
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        //경고 1도 없음
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<String> guys = Set.of("a", "n", "ab");
        Set<String> stooges = Set.of("cd", "ef", "gh");
        Set<String> aflCio = union(guys, stooges);
        System.out.println(aflCio);
    }
}
