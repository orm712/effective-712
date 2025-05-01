package chapter11.item82;


import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class SynchronizedMap {
    public static void main(String[] args) {
        Map<String, Integer> map = new TreeMap<String, Integer>();
        Collections.synchronizedMap(map);
    }
}
