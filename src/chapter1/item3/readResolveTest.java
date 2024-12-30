package chapter1.item3;

// import org.junit.Assert;

import java.io.*;

public class readResolveTest {
    static class withReadResolve implements Serializable {
        public static final withReadResolve INSTANCE = new withReadResolve();
        private withReadResolve() { }
        public void doSomething() { }
        // 싱글턴 프로퍼티를 보장하는 readResolve 메서드
        private Object readResolve() throws ObjectStreamException {
            // 진짜 withReadResolve 객체를 반환하고, 가짜 withReadResolve 객체는 GC에게 맡긴다.
            return INSTANCE;
        }
    }
    static class withoutReadResolve implements Serializable {
        public static final withoutReadResolve INSTANCE = new withoutReadResolve();
        private withoutReadResolve() { }
        public void doSomething() { }
    }
    public static void main(String[] args) throws Exception {
        withReadResolveTest();
        withoutReadResolveTest();
    }
    public static void withoutReadResolveTest() throws Exception{
        withoutReadResolve woRR = withoutReadResolve.INSTANCE;
        FileOutputStream FOS = new FileOutputStream("abc.txt");
        ObjectOutputStream OOS = new ObjectOutputStream(FOS);
        OOS.writeObject(woRR);
        OOS.close();
        FOS.close();
        FileInputStream FIS = new FileInputStream("abc.txt");
        ObjectInputStream OIS = new ObjectInputStream(FIS);
        withoutReadResolve wwoRR = (withoutReadResolve) OIS.readObject();
        // Assert.assertEquals(woRR, wwoRR);
        System.out.println(woRR.equals(wwoRR));
    }
    public static void withReadResolveTest()  throws Exception {
        withReadResolve wRR = withReadResolve.INSTANCE;
        FileOutputStream FOS = new FileOutputStream("abc.txt");
        ObjectOutputStream OOS = new ObjectOutputStream(FOS);
        OOS.writeObject(wRR);
        OOS.close();
        FOS.close();
        FileInputStream FIS = new FileInputStream("abc.txt");
        ObjectInputStream OIS = new ObjectInputStream(FIS);
        withReadResolve  wwRR = (withReadResolve) OIS.readObject();
        // Assert.assertEquals(wRR, wwRR);
        System.out.println(wRR.equals(wwRR));
    }
}
