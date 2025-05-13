package chapter11.item84;

public class ThreadYieldExample {
    public static void main(String[] args) {
        Runnable r = () -> {
            int counter = 0;
            while (counter < 2) {
                //counter가 2 미만이라면, 현재 스레드의 이름을 출력한 뒤 프로세서 사용을 양보
                System.out.println(Thread.currentThread()
                        .getName());
                counter++;
                Thread.yield();
            }
        };
        new Thread(r).start();
        new Thread(r).start();
    }
}