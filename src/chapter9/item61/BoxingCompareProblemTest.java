package chapter9.item61;

import java.util.Comparator;

public class BoxingCompareProblemTest {
    @SuppressWarnings("Deprecated")
    public static void main(String[] args) {
        Comparator<Integer> naturalOrder =
                (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);
        System.out.println(naturalOrder.compare(new Integer(42), new Integer(42)));
        System.out.println(naturalOrder.compare(Integer.valueOf(42), Integer.valueOf(42)));

    }
}
