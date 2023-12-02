package chapter4.item22.badCase;

public class PhysicalConstantsImpl implements PhysicalConstants{
    public static void main(String[] args) {
        // BAD CASE
        // 이러한 방식은 내부 구현사항을 외부에 노출하는 꼴이다.
        double ABC = AVOGADROS_NUMBER + 123.0;
    }
}
