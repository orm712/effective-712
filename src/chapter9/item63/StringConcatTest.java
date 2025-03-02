package chapter9.item63;

public class StringConcatTest {
    private static final int num = 10000;
    // 문자열 연결을 잘못 사용한 예 - 느리다!
    private static String statement() {
        String result = "";
        for (int i = 0; i < 1000000; i++) {
            result += String.valueOf(i);
        }
        return result;
    }

    // 문자열 연결 성능이 크게 개선된다!
    private static String statement2() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(i);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
//        System.out.println(statement());
        System.out.println(statement2());

    }
}
