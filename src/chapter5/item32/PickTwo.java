package chapter5.item32;


import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

// 미묘한 힙 오염 발생
public class PickTwo {
    // 코드 32-2 자신의 제네릭 매개변수 배열의 참조를 노출한다. - 안전하지 않다!
    //메서드가 반환하는 배열의 타입은 메서드에 인수를 넘기는 컴파일 타임에 결정되는데
    //그 시점에는 컴파일러에게 충분한 정보가 주어지지 않는다.
    static <T> T[] toArray(T... args) {
        //Object[] 으로 생성된다.
        //-> String[]로 형변환하면 ClassCastException 발생


        return args;
    }

    static <T> T[] pickTwo(T a, T b, T c) {
        switch(ThreadLocalRandom.current().nextInt(3)) {
            //Object[] 이 반환된다.
            case 0: return toArray(a, b);
            case 1: return toArray(a, c);
            case 2: return toArray(b, c);
        }
        throw new AssertionError(); // 도달할 수 없다.
    }

    public static void main(String[] args) { // (194쪽)
        String[] attributes = pickTwo("좋은", "빠른", "저렴한");
        System.out.println(Arrays.toString(attributes));
    }
}