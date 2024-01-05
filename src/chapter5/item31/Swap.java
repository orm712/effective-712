package chapter5.item31;

import java.util.*;

// 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드 (189쪽)
public class Swap {

    // List<?>에는 null외에 어떤 값도 넣을 수 없다.
//    public static void swap(List<?> list, int i, int j) {
//        list.set(i, list.set(j, list.get(i)));
//    }


    public static void swap(List<?> list, int i, int j) {
        swapHelper(list, i, j);
    }

    // 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }

    public static void main(String[] args) {
        // 첫 번째와 마지막 인수를 스왑한 후 결과 리스트를 출력한다.
        List<String> argList = Arrays.asList(args);
        //클라이언트는 swapHelper의 존재를 모른다.
        swap(argList, 0, argList.size() - 1);
        System.out.println(argList);
    }
}