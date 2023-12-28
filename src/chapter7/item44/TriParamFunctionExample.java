package chapter7.item44;

public class TriParamFunctionExample {
	public static void main(String[] args) {
		TriFunction<Integer, Integer, Integer, Integer> addThreeNumbers = (a, b, c) -> {
			return a + b + c;
		};

		try {
			System.out.println("Result: " + addThreeNumbers.apply(1, 2, 3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
