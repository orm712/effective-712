package chapter1.item7;

import java.util.WeakHashMap;

public class CacheWithWeakHashMap{
    private static WeakHashMap<String, String> map;

    public static void main(String[] args) {
        map = new WeakHashMap<>();
        String key1 = new String("hello");
        map.put(key1, "Kim");

        // null 처리 및 GC 호출로 참조가 해제되도록 유도
        key1 = null;
        System.gc();

        // 동일한 문자열을 기반으로 하는 새로운 키를 사용해 map에서 조회
        String key2 = new String("hello");
        System.out.println(map.get(key2)); // null
    }
}