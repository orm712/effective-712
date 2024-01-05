package chapter6.item40;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Bigram {
    // @Override 애너테이션과 함께 equals와 hashCode를 재정의하는 클래스
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }
    @Override
    public boolean equals(Object o) {
        return first == ((Bigram)o).first && second == ((Bigram)o).second;
    }
    @Override
    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<BigramWithoutAnnotation> s = new HashSet<>();
        Set<Bigram> s2 = new HashSet<>();
        for(int i=0; i<10; i++) {
            for(char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new BigramWithoutAnnotation(ch, ch));
                s2.add(new Bigram(ch, ch));
            }
            System.out.println(s.size());
            System.out.println(s2.size());
            System.out.println(" =============== ");
        }
    }
}

class BigramWithoutAnnotation {
    // @Override 애너테이션 없이 equals와 hashCode를 재정의하려는 클래스
    private final char first;
    private final char second;

    public BigramWithoutAnnotation(char first, char second) {
        this.first = first;
        this.second = second;
    }
    public boolean equals(BigramWithoutAnnotation b) {
        return b.first == first && b.second == second;
    }
    public int hashCode() {
        return 31 * first + second;
    }
}
