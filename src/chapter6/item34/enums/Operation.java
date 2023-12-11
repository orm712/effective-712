package chapter6.item34.enums;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };
    private final String symbol;
    Operation(String symbol) { this.symbol = symbol; }
    @Override public String toString() { return symbol; }
    public abstract double apply(double x, double y);
    private static final Map<String, Operation> stringToEnum =
            Stream.of(values()).collect(toMap(Object::toString, e -> e));
    // Returns Operation for string, if any
    public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }
    public static Operation inverse(Operation op) {
        switch(op) {
            case PLUS: return Operation.MINUS;
            case MINUS: return Operation.PLUS;
            case TIMES: return Operation.DIVIDE;
            case DIVIDE: return Operation.TIMES;
            default: throw new AssertionError("Unknown op: " + op);
        }
    }
}
class test {
    public static void main(String[] args) {
        double x = 20.0;
        double y = 10.0;
        for(Operation op : Operation.values()) {
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
        }
        // 문자열 -> 열거 타입 상수로 변환하는 방법
        // 1. valueOf(String) 사용하기
        System.out.println(Operation.valueOf("PLUS"));
        // 2. fromString(String) 구현해 사용하기
        System.out.println(Operation.fromString("+").get());

        System.out.println(Operation.inverse(Operation.PLUS).apply(20, 10));
    }
}