package chapter6.item40;

public class AnnotationTest extends AnnotationSuper{
    // 만약 상위 클래스의 추상 메서드를 재정의하지 않고있다면, 컴파일러가 이를 알려준다.
    // 따라서, 별도의 @Override를 달지 않아도 된다.
    @Override
    public int abs(int i) {
        return 0;
    }
}

abstract class AnnotationSuper {
    abstract public int abs(int i);
}
