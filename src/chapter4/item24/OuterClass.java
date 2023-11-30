package chapter4.item24;

// 지역 클래스에 대한 예시
public class OuterClass {
	public void display() {
		class LocalClass {
			void message() {
				System.out.println("나는 지역 클래스의 메시지입니다.");
			}
		}
		LocalClass local = new LocalClass();
		local.message();
	}

	public static void main(String[] args) {
		OuterClass outer = new OuterClass();
		outer.display();
	}
}
