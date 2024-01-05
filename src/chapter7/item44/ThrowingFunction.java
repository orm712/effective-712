package chapter7.item44;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
	R apply(T t) throws Exception; // 검사 예외를 던질 수 있음
}
