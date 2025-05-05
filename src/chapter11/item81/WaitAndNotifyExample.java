package chapter11.item81;

// 참고 코드
// https://www.geeksforgeeks.org/difference-between-wait-and-notify-in-java/

public class WaitAndNotifyExample {
    static class demo {
        // variable to check if part1 has returned
        // volatile used to prevent threads from
        // storing local copies of variable
        volatile boolean isSent = false;

        // 현재 클래스 인스턴스(this)에 대해 동기화 됨
        synchronized void send()
        {
            System.out.println("메세지 송신함");
            isSent = true;
            System.out.println("Thread t1이 락을 놓아주려 함");
            notify(); // = this.notify()
        }

        // 현재 클래스 인스턴스(this)에 대해 동기화 됨
        synchronized void receive()
        {
            // loop to prevent spurious wake-up
            while (!isSent) {
                try {
                    System.out.println("Thread t2 기다림");
                    wait(); // = this.wait();
                }
                catch (Exception e) {
                    System.out.println(e.getClass());
                }
            }
            System.out.println("메세지 도착함");
        }
    }
    public static void main(String[] args)
    {
        demo obj = new demo();
        Thread t1 = new Thread(() -> obj.send());
        Thread t2 = new Thread(() -> obj.receive());

        t2.start();
        t1.start();
    }
}
