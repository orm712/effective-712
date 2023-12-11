package chapter6.item37;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnumMapCase {
    public static void main(String[] args) {
        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle =
                new EnumMap<Plant.LifeCycle, Set<Plant>>(Plant.LifeCycle.class);
        // 각 생애주기 enum들을 key로, HashSet을 EnumMap에 Mapping한다.
        for (Plant.LifeCycle lc : Plant.LifeCycle.values())
            plantsByLifeCycle.put(lc, new HashSet<>());
        Plant garden[] = new Plant[] {new Plant("Iris", Plant.LifeCycle.ANNUAL)};
        for (Plant p : garden)
            plantsByLifeCycle.get(p.lifeCycle).add(p);
        System.out.println(plantsByLifeCycle);
    }
}
