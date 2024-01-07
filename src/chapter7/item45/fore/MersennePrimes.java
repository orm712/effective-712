package chapter7.item45.fore;

import java.math.BigInteger;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;

public class MersennePrimes {

	// 무한스트림 반환
	static Stream<BigInteger> primes() {
		return Stream.iterate(TWO, BigInteger::nextProbablePrime);
		// nextProbablePrime() -> 다음 소수를 반환합니다. 반환값이 소수가 아닐 확률은 2의 -100승을 초과하지 않습니다.
	}

	// 메르센 소수는 2^p -1 형태의 소수이며, p가 소수일 때 메르센 수도 소수일 수 있는데 이 때 소수면 메르센 소수이다.
	// 20개의 메르센 소수를 출력하는 프로그램이다.
	public static void main(String[] args) {
		primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
				.filter(mersenne -> mersenne.isProbablePrime(50))
				.limit(20)
				.forEach(System.out::println);
//				.forEach(mp -> System.out.println(mp.bitLength() + ": " + mp));
	}
	// 위에서 각 메르센 소수 앞에 지수p를 출력하고 싶다면?
	// 이 p는 초기 스트림에만 나타나므로 결과를 출력하는 종단 연산에서 접근 불가능하다.
	// 하지만 첫 중간 연산에서 수행한 매핑을 거꾸로 수행하면 쉽게 계산해낼 수 있다.
	// 중간연산이 많고 복잡해진다면, 이런 식으로 쉽게 최초 스트림 원소에 접근하기 힘들 것이다.
}
