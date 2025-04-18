package chapter10.item73;

public class ExceptionChainingExample {
    static class Deck {
        int cardNumber = 10;
        int[] cards = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        public void draw() {
            System.out.println("Card Draw!" + cards[cardNumber--]);
        }
    }
    public static void main(String[] args) {
        ExceptionTranslateExample.Deck newDeck = new ExceptionTranslateExample.Deck();
        try {
            while(true) {
                newDeck.draw();
            }
        } catch (IndexOutOfBoundsException cause) {
            // 내부 상태에 의해 발생한 예외이므로 IllegalStateException 로 번역하되
            // 근본 원인(IndexOutOfBoundsException)을 담아 던짐
            throw new IllegalStateException(cause);
        }
    }
}
