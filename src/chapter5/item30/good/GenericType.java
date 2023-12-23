package chapter5.item30.good;

import java.util.HashSet;
import java.util.Set;

public class GenericType {
    // public static Set union(Set s1, Set s2) {
    //제네릭 타입이나 제네릭 메서드나 적용하는 방식은 똑같다.
    //앞에 <E>는 메서드가 제네릭 타입 E를 사용한다는 것을 선언한다.
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
