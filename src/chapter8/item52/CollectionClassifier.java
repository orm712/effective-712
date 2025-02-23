package chapter8.item52;

import java.math.BigInteger;
import java.util.*;

// 컬렉션 분류기
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "Set";
    }

    public static String classify(List<?> lst) {
        return "List";
    }

    public static String classify(Collection<?> c) {
        return "Unknown Collection";
    }

    public static String classify_v2(Collection<?> c) {
        return c instanceof Set<?> ? "Set" :
                c instanceof List ? "List" : "Unknown Collection";
    }
    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };
        // 일반 사례
        for (Collection<?> c : collections)
            System.out.println(classify(c));

        // 개선 사례
        for (Collection<?> c : collections)
            System.out.println(classify_v2(c));
    }
}