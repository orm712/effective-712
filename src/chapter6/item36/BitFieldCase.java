package chapter6.item36;

public class BitFieldCase {
    // 글의 스타일을 정하기 위해 정수 열거 패턴을 사용한 사례
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8
    // STYLE_ 상수들을 OR 연산을 통해 결합한 비트 필드인 'styles' 인수를 받는 함수
    public static void applyStyles(int styles) {
        // styles에 포함된 상수들을 알려면 일일히 비트 연산을 해주어야 한다.
        if((styles & STYLE_BOLD) == STYLE_BOLD) {
            System.out.println("STYLE_BOLD");
        }
        if((styles & STYLE_ITALIC) == STYLE_ITALIC) {
            System.out.println("STYLE_ITALIC");
        }
        if((styles & STYLE_UNDERLINE) == STYLE_UNDERLINE) {
            System.out.println("STYLE_UNDERLINE");
        }
        if((styles & STYLE_STRIKETHROUGH) == STYLE_STRIKETHROUGH) {
            System.out.println("STYLE_STRIKETHROUGH");
        }
    }


    public static void main(String[] args) {
        // 사용 예시
        applyStyles(STYLE_BOLD | STYLE_ITALIC);
    }
}
