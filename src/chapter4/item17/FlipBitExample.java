package chapter4.item17;

import java.math.BigInteger;
import java.util.BitSet;

public class FlipBitExample {
    public static void main(String[] args) {
        BigInteger test = new BigInteger(1, new byte[1_000_000]);
        // 한 비트만 다른 새로운 BigInteger 인스턴스 생성
        test = test.flipBit(0);

        // 가변 비트 순열인 BitSet
        BitSet bitSet = new BitSet(1_000_000);
        // BitSet 의 flip 연산은 상수 시간 안에 비트 하나를 바꿔줌
        bitSet.flip(0);

    }
}
