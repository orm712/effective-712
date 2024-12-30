package chapter1.item3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ElvisTest {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor elvisConstructor = Class.forName("chapter1.item3.Elvis").getDeclaredConstructor();
        elvisConstructor.setAccessible(true);
        Elvis elvis = (Elvis) elvisConstructor.newInstance();
    }
}
class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { }
    public void leaveTheBuilding() { }
}
