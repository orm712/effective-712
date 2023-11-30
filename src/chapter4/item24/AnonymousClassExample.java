package chapter4.item24;

public class AnonymousClassExample {
	public static void main(String[] args) {

		Runnable r = new Runnable() {
			@Override
			public void run() {
				System.out.println("익명 클래스의 run 메소드");
			}
		};

		new Thread(r).start();
	}
}
