package chapter7.item45.one;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample1 {
	public static void main(String[] args) {
		// 스트림 생성
		List<Integer> numberList = Arrays.asList(1, 2, 3, 4, 5);

		// 스트림을 사용하여 각 원소에 2를 곱하고 결과를 리스트로 수집
		List<Integer> doubled = numberList.stream()
				.map(n -> n * 2)
				.collect(Collectors.toList());

		System.out.println(doubled); // [2, 4, 6, 8, 10]
	}
}
