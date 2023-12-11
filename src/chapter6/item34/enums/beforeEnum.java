package chapter6.item34.enums;

public class beforeEnum {
    // 정수 열거 패턴
    public static final int APPLE_FUJI = 0;
    public static final int APPLE_PIPPIN = 1;
    public static final int APPLE_GRANNY_SMITH = 2;

    public static final int ORANGE_NAVEL = 0;
    public static final int ORANGE_TEMPLE = 1;
    public static final int ORANGE_BLOOD = 2;

    // 문자열 열거 패턴
    // 문자열 값을 하드코드 한다.
    public static final String ORANGE_NASHVILLE = "ORANGE_NASHVILLE";

   public static void main(String[] args) {
       // 정수 열거 패턴의 단점
       // 1. 타입 안전을 보장할 수 없다.
       // 오렌지를 인자로 받는 함수에 사과를 넣어도 함수에서는 파악 불가능하다.
       squeeze(APPLE_FUJI);
       // 문자열로 출력하기 까다롭다.
       // 해당 값을 출력해도 의미 있는 값을 알 수 없다.
       System.out.println(APPLE_FUJI);
    }
    public static void squeeze(int ORANGE) {
       if(ORANGE == ORANGE_NAVEL) {
           System.out.println("Squeeze Orange");
       }
    }
}