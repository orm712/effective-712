package chapter6.item36;

import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

public class EnumSetCase {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
    // Style enum들로 이뤄진 Set을 인수로 받는 함수
    // 클라이언트가 EnumSet을 인자로 넘길것으로 예상되어도
    // 인터페이스로 받는게 더 좋은 습관이다.
    public static void applyStyles(Set<Style> styles) {
        for(Style s : styles) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) {
//        Set<Style> set = new TreeSet<>();
//        for(Style s : Style.values()) {
//            set.add(s);
//        }
//        applyStyles(set);
        // EnumSet의 정적 팩토리 메서드 'of' 를 사용해 Set을 만드는 코드
        applyStyles(EnumSet.of(Style.BOLD, Style.UNDERLINE));
    }
}
