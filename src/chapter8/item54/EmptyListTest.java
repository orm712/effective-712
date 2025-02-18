package chapter8.item54;

import java.util.Collections;
import java.util.List;

public class EmptyListTest {
    public static void main(String[] args) throws Exception {
        List<Object> emptyList = Collections.emptyList();
        System.out.println(emptyList.getClass());
        emptyList.add("1");

    }
}
