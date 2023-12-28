package chapter7.item44;

public class ThrowFunctionalInterfaceExample {
	public static void main(String[] args) {
		ThrowingFunction<Integer, String> function = number -> {
			if (number < 0) {
				throw new Exception("Number must be non-negative");
			}
			return "Number is " + number;
		};

		try {
			System.out.println(function.apply(5));
			System.out.println(function.apply(-1));
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
