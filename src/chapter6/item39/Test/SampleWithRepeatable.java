package chapter6.item39.Test;

public class SampleWithRepeatable {
    @ExceptionWithRepeatable(ArithmeticException.class)
    public static void m1() {
        // 성공해야 한다. (ArithmeticException 예외가 발생하므로)
        int i = 0;
        i = i / i;
    }
    @ExceptionWithRepeatable(ArithmeticException.class)
    @ExceptionWithRepeatable(ArrayIndexOutOfBoundsException.class)
    public static void m2() {
        // 실패해야 한다. 다른 예외가 발생했으므로
        int a[] = new int[0];
        int i = a[1];
    }
    @ExceptionWithRepeatable(ArithmeticException.class)
    @ExceptionWithRepeatable(ArrayIndexOutOfBoundsException.class)
    public static void m3() {
        // 실패해야 한다. 예외가 발생하지 않았으므로
    }
}
