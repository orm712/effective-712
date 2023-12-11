package chapter6.item37;

public class ordinal2DArrayCase {
    // 2차원 배열의 인덱스에서 enum의 ordinal을 사용하는 예제
    // 이 방식의 단점

    // 1. 컴파일러가 ordinal과 배열의 index 간의 관계를 알 수 없다.
    // 즉, Phase 또는 Transition을 수정하고 TRANSITIONS를 함께 수정하지 않으면
    // 오작동 할 수 있다.

    // 2. 배열의 크기가 상태의 가짓수의 제곱으로 커지게 된다.
    // 현재 Phase는 3가지 가짓수로 TRANSITIONS가 3x3인데, Phase 가짓수가 늘어나면
    // TRANSITIONS도 Phase 가짓수 ^ 2로 크기가 늘어나게 된다.
    public enum Phase {
        SOLID, LIQUID, GAS;
        public enum Transition {
            MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;
            // Rows indexed by from-ordinal, cols by to-ordinal
            private static final Transition[][] TRANSITIONS = {
                    { null, MELT, SUBLIME },
                    { FREEZE, null, BOIL },
                    { DEPOSIT, CONDENSE, null }
            };
            // Returns the phase transition from one phase to another
            public static Transition from(Phase from, Phase to) {
                return TRANSITIONS[from.ordinal()][to.ordinal()];
            }
        }
    }
    public static void main(String[] args) {
        System.out.println(Phase.Transition.from(Phase.SOLID, Phase.GAS));
    }
}
