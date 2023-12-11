package chapter6.item37;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumMap2DCase {
    public enum Phase {
        SOLID, LIQUID, GAS;
        public enum Transition {
            // 각 전이 상태들은 from, to 상태를 인자로 받아 생성된다.
            MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
            BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
            SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);
            private final Phase from;
            private final Phase to;
            Transition(Phase from, Phase to) {
                this.from = from;
                this.to = to;
            }
            // Initialize the phase transition map
            // Transition의 from(Phase)을 Key로하고,
            // [to(Phase)를 Key로 하고 Transition를 value로 받는 EnumMap]을 value로 받는 맵 m
            // 이때, toMap의 MergeFunction으로 들어간 `(x, y) -> y` 는 실제로 쓰이지 않는데,
            // EnumMap을 얻으려면 mapFactory(toMap의 4번째 인자)가 필요하고,
            // Collectors는 점층적 팩토리(telescoping factory)를 제공하기 때문이다.
            private static final Map<Phase, Map<Phase, Transition>>
                    m = Stream.of(values()).collect(Collectors.groupingBy(t -> t.from,
                    () -> new EnumMap<>(Phase.class),
                    Collectors.toMap(t -> t.to, t -> t,
                            (x, y) -> y, () -> new EnumMap<>(Phase.class))));
            public static Transition from(Phase from, Phase to) {
                return m.get(from).get(to);
            }}}
}
