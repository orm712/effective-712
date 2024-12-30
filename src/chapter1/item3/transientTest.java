package chapter1.item3;

import java.io.*;

public class transientTest {
    static class withTransient implements Serializable {
        int var1 = 20;
        int var2 = 30;

        @Override
        public String toString() {
            return "withTransient{" +
                    "var1=" + var1 +
                    ", var2=" + var2 +
                    '}';
        }
    }
    static class withoutTransient implements Serializable {
        transient int var1 = 20;
        transient int var2 = 30;

        @Override
        public String toString() {
            return "withoutTransient{" +
                    "var1=" + var1 +
                    ", var2=" + var2 +
                    '}';
        }
    }
    public static void main(String[] args) throws Exception {
        testWithTransient();
        testWithoutTransient();
    }
    public static void testWithTransient() throws Exception{
        withTransient wT = new withTransient();
        FileOutputStream FOS = new FileOutputStream("abc.txt");
        ObjectOutputStream OOS = new ObjectOutputStream(FOS);
        OOS.writeObject(wT);
        OOS.close();
        FOS.close();
        FileInputStream FIS = new FileInputStream("abc.txt");
        ObjectInputStream OIS = new ObjectInputStream(FIS);
        withTransient wwT = (withTransient) OIS.readObject();
        System.out.println(wwT);
        FIS.close();
        OIS.close();
    }
    public static void testWithoutTransient() throws Exception {
        withoutTransient woT = new withoutTransient();
        FileOutputStream FOS = new FileOutputStream("abc.txt");
        ObjectOutputStream OOS = new ObjectOutputStream(FOS);
        OOS.writeObject(woT);
        OOS.close();
        FOS.close();
        FileInputStream FIS = new FileInputStream("abc.txt");
        ObjectInputStream OIS = new ObjectInputStream(FIS);
        withoutTransient wwoT = (withoutTransient) OIS.readObject();
        System.out.println(wwoT);
        FIS.close();
        OIS.close();
    }
}
