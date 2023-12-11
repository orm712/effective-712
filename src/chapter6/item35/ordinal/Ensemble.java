package chapter6.item35.ordinal;

public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);
    private final int numberOfMusicians;
    Ensemble(int size) { this.numberOfMusicians = size; }
    public int numberOfMusicians() { return numberOfMusicians; }
}
class ensembleTest {
    public static void main(String[] args) {
        // ordinal()을 사용한 사례
        // enum 간 순서가 바뀌면 값도 바뀌게 된다.
        System.out.println(Ensemble.SOLO.ordinal());
        System.out.println(Ensemble.OCTET.ordinal());
        // 인스턴스 필드를 사용한 사례
        System.out.println(Ensemble.OCTET.numberOfMusicians());
    }
}
