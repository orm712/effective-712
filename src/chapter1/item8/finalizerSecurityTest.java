package chapter1.item8;

import java.lang.reflect.Field;

public class finalizerSecurityTest {
    public static Class normalClass;
    public static void main(String[] args) throws Exception{
        try {
            MaliciousSubClass maliciousSubClass = new MaliciousSubClass();
        } catch (Exception e) {
            System.gc();
            Thread.sleep(5000);
        }
    }
}
class Class {
    private Integer i;
    private String s;
    Class() throws Exception {
        i = Integer.valueOf(20);
        if(true) throw new Exception();
        s = new String("하이");
    }
    public void criticalAction() {
        System.out.println("핵심 동작을 수행");
        System.out.println(s);
    }

    @SuppressWarnings("removal")
    public void finalize() {}
}

class MaliciousSubClass extends Class {
    MaliciousSubClass() throws Exception {
    }

    public void finalize() {
        System.out.println("악의적인 하위 클래스의 finalizer 호출");
        try{
            // main문이 실행되는 클래스의 정적 필드 접근 및
            Field field = finalizerSecurityTest.class.getDeclaredField("normalClass");
            field.setAccessible(true);
            // 현재 MaliciousSubClass 인스턴스를 해당 필드 값으로 할당
            field.set(null, this);
            // 치명적인 작업 수행
            criticalAction();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}