package chapter3.item10;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성 위배
    @Override public boolean equals(Object o) {
        // 주어진 o가 CaseInsensitiveString 인스턴스인 경우
        if (o instanceof CaseInsensitiveString)
            // 해당 인스턴스의 s와 현재 인스턴스의 s를 대소문자 상관없이 비교
            return s.equalsIgnoreCase(
                    ((CaseInsensitiveString) o).s);
        if (o instanceof String) // 단방향 상호 운용성
            return s.equalsIgnoreCase((String) o);
        return false;
    }
    public static void main(String[] args) {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
        String s = "polish";

        System.out.println(cis.equals(s)); // true
        System.out.println(s.equals(cis)); // false

        List<CaseInsensitiveString> list = new ArrayList<>();
        list.add(cis);

        System.out.println(list.contains(s)); // false
    }
}
