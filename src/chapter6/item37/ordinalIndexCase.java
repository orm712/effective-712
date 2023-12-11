package chapter6.item37;

import java.util.HashSet;
import java.util.Set;

public class ordinalIndexCase {
    public static void main(String[] args) {
        // 생명주기의 가짓수 만큼 크기를 갖는 Set<Plant>의 배열을 선언한다.
        Set<Plant>[] plantsByLifeCycle =
                (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        // 식물들의 집합인 garden을 선언한다.
        Plant garden[] = new Plant[] {new Plant("Iris", Plant.LifeCycle.ANNUAL)};
        // 각 Set<Plant>를 초기화 한다.
        for (int i = 0; i < plantsByLifeCycle.length; i++)
            plantsByLifeCycle[i] = new HashSet<>();
        // garden을 돌며, 현재 식물 p의 생명주기에 해당하는,
        // 즉 p의 생명주기의 ordinal()와 일치하는 index를 가진 Set에 p를 추가한다.
        for (Plant p : garden)
            plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);
// Print the results
        // 각 생명주기별 식물들을 출력한다.
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.printf("%s: %s%n",
                    Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
class Plant {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }
    final String name;
    final LifeCycle lifeCycle;
    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }
    @Override public String toString() {
        return name;
    }
}