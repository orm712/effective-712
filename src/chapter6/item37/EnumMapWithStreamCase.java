package chapter6.item37;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class EnumMapWithStreamCase {
    public static void main(String[] args) {
        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle =
                new EnumMap<Plant.LifeCycle, Set<Plant>>(Plant.LifeCycle.class);
        // 각 생애주기 enum들을 key로, HashSet을 EnumMap에 Mapping한다.
        for (Plant.LifeCycle lc : Plant.LifeCycle.values())
            plantsByLifeCycle.put(lc, new HashSet<>());
        Plant garden[] = new Plant[] {new Plant("Iris", Plant.LifeCycle.ANNUAL)};
        // Stream을 사용한 예시
        System.out.println(Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle)));
        // Stream과 EnumMap을 사용한 예시
        System.out.println(Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle,
                () -> new EnumMap<>(Plant.LifeCycle.class), toSet())));
    }
}
