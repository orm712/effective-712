package chapter5.item30.bad;

import java.util.HashSet;
import java.util.Set;

public class RawType {
    public static Set union(Set s1, Set s2) {

        //타입 안전하게 만들자!
        Set result = new HashSet(s1);
        result.addAll(s2);
        return s1;
    }
}
