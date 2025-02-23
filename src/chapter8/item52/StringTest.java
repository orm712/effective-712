package chapter8.item52;

public class StringTest {
    public static void main(String[] args) {
        char[] arr = {'하', '이', '요'};

        // 1. String.valueOf(Object) 를 호출한 경우
        // 객체 티압과 해시코드를 출력
        System.out.println(String.valueOf((Object) arr));

        // 2. String.valueOf(char[]) 를 호출한 경우
        // char 배열의 캐릭터들을 문자열로 변환해 출력
        System.out.println(String.valueOf(arr));
    }
}
