package chapter5.item32;

import java.util.ArrayList;
import java.util.List;

// 코드 32-3 제네릭 varargs 매개변수를 안전하게 사용하는 메서드
public class FlattenWithVarargs {
    @SafeVarargs
    static <T> List<T> flatten(List<? extends T>... lists) {

        //받은 순서대로 그 안의 모든 원소를 하나의 리스트로 옮겨 담아 반환한다.
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists)
            result.addAll(list);
        return result;
    }

    public static void main(String[] args) {
        List<Integer> flatList = flatten(
                List.of(1, 2), List.of(3, 4, 5), List.of(6,7));
        System.out.println(flatList);
    }
}