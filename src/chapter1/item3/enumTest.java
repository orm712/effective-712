package chapter1.item3;

// import org.junit.Assert;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class enumTest {
    public static void main(String[] args) throws Exception{
        ElvisEnum EE = ElvisEnum.INSTANCE;
        FileOutputStream FOS = new FileOutputStream("abc.txt");
        ObjectOutputStream OOS = new ObjectOutputStream(FOS);
        OOS.writeObject(EE);
        OOS.close();
        FOS.close();
        FileInputStream FIS = new FileInputStream("abc.txt");
        ObjectInputStream OIS = new ObjectInputStream(FIS);
        ElvisEnum EEE = (ElvisEnum) OIS.readObject();
        // Assert.assertEquals(EE, EEE);
        System.out.println(EE.equals(EEE));
    }
}

enum ElvisEnum {
    INSTANCE;

    public void doSomething() {}
}
