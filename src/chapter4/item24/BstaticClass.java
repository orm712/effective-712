package chapter4.item24;

public class BstaticClass {
	private String outerField = "바깥 클래스의 필드";

	class InnerClass {
		void display() {
			// 정규화된 this를 사용하여 바깥 클래스의 필드에 접근
			System.out.println("바깥 클래스의 필드: " + BstaticClass.this.outerField);
		}
	}

	public static void main(String[] args) {
		BstaticClass outer = new BstaticClass();
		InnerClass inner = outer.new InnerClass(); // 바깥 클래스의 인스턴스를 통해 내부 클래스의 인스턴스 생성
		inner.display();
	}
}
