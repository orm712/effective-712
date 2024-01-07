package chapter7.item45.one;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StreamExample2 {
	public static void main(String[] args) {
		// 스트림 생성
		List<String> stringList = Arrays.asList("apple", "banana", "cherry", "date");

		// 스트림을 사용하여 문자열 길이가 5 이상인 첫 번째 원소를 찾기
		Optional<String> longString = stringList.stream()
				.filter(s -> s.length() >= 5)
				.findFirst();

		longString.ifPresent(System.out::println); // banana
	}
}
