package chapter6.item41;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

class ABC {
    int a;
    ABC(int a) {
        this.a = a;
    }
}
public class writeObjectExample {
    public static void main(String[] args) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("hi"));
        // Serializable을 implements 하지 않은 객체도 인자로 넘길 수 있다.
        oos.writeObject(new ABC(10));
    }
}
