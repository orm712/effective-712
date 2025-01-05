package chapter1.item8;

import java.lang.ref.Cleaner;

public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // 청소가 필요한 자원. 절대로 Room을 참조해서는 안 된다!
    private static class State implements Runnable {
        int numJunkPiles; // 방(room) 안의 쓰레기 수
        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }
        // close 메서드 or cleaner에 의해 호출된다.
        @Override
        public void run() {
            System.out.println("Cleaning room");
            numJunkPiles = 0;
        }
    }
    // 방의 상태를 나타내는 변수. cleanable과 공유한다.
    private final State state;

    // cleanable 객체. gc의 대상이 되면 방을 청소한다.
    private final Cleaner.Cleanable cleanable;
    // Room의 생성자
    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        // cleaner에 room(this)과 state를 등록해 cleanable 객체를 얻는다.
        cleanable = cleaner.register(this, state);
    }
    @Override public void close() {
        cleanable.clean();
    }

    public static void main(String[] args) {
        Adult();
        Teenager();
    }
    public static void Adult() {
        try (Room myRoom = new Room(7)) {
            System.out.println("Goodbye");
        }
    }
    public static void Teenager() {
        new Room(99);
        System.out.println("Peace out");
//        System.gc();
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}