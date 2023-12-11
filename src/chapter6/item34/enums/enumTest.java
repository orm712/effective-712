package chapter6.item34.enums;

public class enumTest {
    public enum testEnum {
        A,
        B,
        C
    }
    public enum testEnumTwo {
        A,
        B,
        C
    }
    public static void main(String[] args) {
        // enum 타입은 접근 가능한 생성자를 제공하지 않는다.
        // 따라서 확장 불가능하고, 추가 인스턴스가 존재할 수 없다.
        // "Enum types cannot be instantiated"
        // enum abc = new testEnum();

        // 열거 타입의 장점
        // 1. 타입 안정성을 제공한다.
        // 따라서 다른 타입의 enum을 넘기려고 하면 오류가 발생한다.
//        testMethod(testEnumTwo.C);
        // 2. 각자의 namespace가 존재한다.
        System.out.println(testEnum.A);
        System.out.println(testEnumTwo.A);
        // 또한, 출력하면 해당 열거 타입의 이름을 출력해준다.
        testMethod(testEnum.A);
        for(testEnum t : testEnum.values()) {
            System.out.println(t);
        }
    }
    public static void testMethod(testEnum abc) {
        System.out.println(abc);
    }
}
