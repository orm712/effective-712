package chapter4.item19;

public class Super {

    private String name;
    public Super() {
        System.out.println("부모 - 매개변수가 없는 생성자");
    }

    public Super(String name) {
        System.out.println("부모 - 매개변수가 있는 생성자");
    }
}

class Sub extends Super {

    private String name;

    public Sub(String name) {
        // super(); 생략된 상태 - 매개변수가 없는 생성자 호출
        // super(name); 직접 명시 - 매개변수가 있는 생성자 호출
        System.out.println("자식");
    }

}

class Main {
    public static void main(String[] args) {
        Sub sub = new Sub("test");
    }
}