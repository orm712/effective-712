package chapter4.item22.goodCase;

public class constantsWithClass {
    // 1. 자주 사용되는 상수를 해당 클래스/인터페이스 자체에 추가하여 사용하는 방식.
    // 특정 클래스/인터페이스에 자주 사용되는 상수는 해당 클래스/인터페이스 자체에 추가해 사용한다.
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    public static void main(String[] args) {
        double example = AVOGADROS_NUMBER + 123.0;
        // Integer 클래스에 존재하는 MAX_VALUE 가 이러한 방식의 대표 예시다.
        int example2 = Integer.MAX_VALUE;

        // 2. enum 타입을 사용하는 방식.
        constantsWithEnum example3 = constantsWithEnum.BOTH;

        // 3. 유틸리티 클래스를 사용하는 방식.
        double example4 = constantsWithStaticClass.AVOGADROS_NUMBER;
    }
}
