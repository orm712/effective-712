package chapter10.item69;

public class WrongArrayLoopExceptionExample {
    public static void main(String[] args) {
        Mountain[] range = new Mountain[10];
        for(int i=0; i<range.length; i++) {
            range[i] = new Mountain();
        }
        try {
            int i = 0;
            while(true) {
                range[i++].climb(i);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // climb 가 호출한 anotherMethod 가 Mountain 내부의 다른 배열을 잘못 참조해
            // ArrayIndexOutOfBoundsException 가 발생했지만, 정상적인 반복문 종료로 인식함 
        }
    }
    static class Mountain {
        static int[] arr = new int[] {10, 20, 30};
        public void climb(int i) {
            System.out.println("Climbed the Mountain!");
            anotherMethod(i);
        }
        public void anotherMethod(int i) {
            System.out.println(arr[i]);
        }
    }
}
