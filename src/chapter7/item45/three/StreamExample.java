package chapter7.item45.three;

import java.util.Arrays;
import java.util.List;

public class StreamExample {
	public static void main(String[] args) {
		List<String> items = Arrays.asList("apple", "banana", "cherry");

		items.stream()
				.filter(item -> "banana".equals(item))
				.findFirst()
				.ifPresent(item -> System.out.println("Banana found!"));
		// 'break' 또는 'continue'와 같은 제어는 사용할 수 없음
	}
}
