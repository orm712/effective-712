package chapter7.item45.two;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IterativeAnagrams {
	public static void main(String[] args) throws IOException {
		File dictionary = new File(args[0]);
		int minGroupSize = Integer.parseInt(args[1]);

		Map<String, Set<String>> groups = new HashMap<>();
		try (Scanner s = new Scanner(dictionary)) {
			while (s.hasNext()) {
				String word = s.next();

				// computeIfAbsent
				// 특정 키에 대한 값이 맵에 존재하지 않을 때만 계산을 수행하고 그 결과를 맵에 저장
				groups.computeIfAbsent(alphabetize(word),
						(unused) -> new TreeSet<>()).add(word);
			}
		}

		for (Set<String> group : groups.values())
			if (group.size() >= minGroupSize)
				System.out.println(group.size() + ": " + group);
	}

	private static String alphabetize(String s) {
		char[] a = s.toCharArray();
		Arrays.sort(a);
		return new String(a);
	}
}
