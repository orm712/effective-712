package chapter7.item44;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
	R apply(T t, U u, V v) throws Exception;
}
