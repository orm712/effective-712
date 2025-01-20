package chapter4.item15;

public class PrivateStaticClassExample {
    // 한 클래스에서만 사용되는 최상위 package-private 클래스/인터페이스를
    // 이를 사용하는 클래스 내부의 private static 클래스로 선언
    private static class InnerClass {
        public int x;
        public int y;
    }

    public static void main(String[] args) {
        InnerClass innerClass = new InnerClass();
        innerClass.x = 10;
    }
}

class AnotherClassFromWithinPackage {
    // 패키지 내 다른 클래스에서는 private static 클래스를 참조할 수 없음
    // InnerClass innerClass;
}