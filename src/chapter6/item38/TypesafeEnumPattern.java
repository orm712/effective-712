package chapter6.item38;

public class TypesafeEnumPattern {
    // 타입 안전 열거 패턴(typesafe enum type)
    // `정수 열거 패턴`의 단점들을 대체하기 위해 클래스-기반 대안으로 제시 되었다.
    // 아래 Suit는 이러한 타입 안전 열거 패턴의 한 예시로, 카드 슈트를 의미한다.
    // 카드 슈트 -> 클럽, 다이아몬드, 하트, 스페이드
    public static class Suit {
        private final String name;

        public static final Suit CLUBS = new Suit("clubs");
        public static final Suit DIAMONDS = new Suit("diamonds");
        public static final Suit HEARTS = new Suit("hearts");
        public static final Suit SPADES = new Suit("spades");

        private Suit(String name){
            this.name =name;
        }
        public String toString(){
            return name;
        }
    }

    public static void main(String[] args) {
        // 타입 안전 열거 패턴의 단점
        // 1. 상수들을 하나의 집합으로 묶는 것이 어색하다.
        // 2. switch 문에서 사용할 수 없다.
        Suit suit = Suit.DIAMONDS;
        // Incompatible types. Found: 'chapter6.item38.TypesafeEnumPattern.Suit',
        // required: 'char, byte, short, int, Character, Byte, Short, Integer, String, or an enum'
//        switch (suit)
//        {
//            case Suit.CLUBS   : System.out.println("clubs"); break;
//            case Suit.DIAMONDS: System.out.println("diamonds"); break;
//            case Suit.HEARTS  : System.out.println("hearts"); break;
//            case Suit.SPADES  : System.out.println("spades");
//        }

    }
}
