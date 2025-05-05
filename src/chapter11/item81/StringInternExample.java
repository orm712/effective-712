package chapter11.item81;

public class StringInternExample {
    public static void main(String[] args) {
        whenIntern_thenCorrect();
    }
    public static void whenIntern_thenCorrect() {
        String s1 = "abc";
        String s2 = new String("abc");
        String s3 = new String("foo");
        String s4 = s1.intern();
        String s5 = s2.intern();

        System.out.println(s3 == s4);
        System.out.println(s1 == s2);
        System.out.println(s1 == s5);
    }
}
