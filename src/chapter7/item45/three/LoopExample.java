package chapter7.item45.three;

import java.util.Arrays;
import java.util.List;

public class LoopExample {
	public static void main(String[] args) {
		List<String> items = Arrays.asList("apple", "banana", "cherry");

		for (String item : items) {
			if ("banana".equals(item)) {
				System.out.println("Banana found!");
				break; // 반복문 제어
			}
		}
	}
}
