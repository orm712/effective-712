package chapter10.item76;

import java.util.ArrayList;
import java.util.List;

public class ListSortExample {
    public static void main(String[] args) {
        List<Integer> list = List.of(10, 1, 2, 3, 4, 5, 6, 7);
        System.out.println(sort(list));
    }
    public static List sort(List list) {
         Object[] arr = list.toArray();
         java.util.Arrays.sort(arr);
         return List.of(arr);
    }
}
