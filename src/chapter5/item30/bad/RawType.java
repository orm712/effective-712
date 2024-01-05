package chapter5.item30.bad;

import java.util.HashSet;
import java.util.Set;

public class RawType {
    public static Set union(Set s1, Set s2) {
        //로타입을 사용한다.
        // 경고 발생!
        //타입 안전하게 만들자!(입력 2개, 반환 1개) -> Generic Type 참고
        Set result = new HashSet(s1);
        result.addAll(s2);
        return s1;
    }
}
