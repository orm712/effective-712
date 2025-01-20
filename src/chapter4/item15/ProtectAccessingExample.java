package chapter4.item15;

public class ProtectAccessingExample {
    public static void main(String[] args) {
        S s = new S();
        // 1. Q.Id의 형태로 접근
        System.out.println(s.p_int);

        // 2. E.Id의 형태로 접근
        System.out.println(new S().p_int);
    }
}

class C {
    protected int p_int = 20;
    public C() {}
    protected C(int i) {
        this.p_int = i;
    }
}

class S extends C{
    public S() {
        this.p_int = 30;
    }
    protected S(int i, int j) {
        super(i);
        this.p_int *= j;
    }
}