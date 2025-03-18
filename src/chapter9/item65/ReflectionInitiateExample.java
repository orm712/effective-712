package chapter9.item65;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

public class ReflectionInitiateExample {
    public static void main(String[] args) {
        // 클래스 이름을 기반으로 Class 객체 획득
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) // 비검사 형변환(uncheked cast). 이로 인해 컴파일 시 비검사 형변환 경고가 표시됨
                    Class.forName(args[0]);
        } catch (ClassNotFoundException e) {
            fatalError("Class not found.");
        }

        // 생성자 획득
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fatalError("No parameterless constructor");
        }
        // set 인스턴트 생성
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            fatalError("Constructor not accessible");
        } catch (InstantiationException e) {
            fatalError("Class not instantiable.");
        } catch (InvocationTargetException e) {
            fatalError("Constructor threw " + e.getCause());
        } catch (ClassCastException e) {
            fatalError("Class doesn't implement Set");
        }
        // 또는, ReflectiveOperationException 로 묶어 예외를 처리할 수도 있음
        // try {
        //      s = cons.newInstance();
        // } catch (ReflectiveOperationException roe) {
        //            System.out.println(roe.getCause());
        // }

        // set 사용
        // 커맨드라인 인수의 두 번째 인수부터 마지막 인수까지를 Set에 추가한 뒤 출력
        // 이때, 출력되는 순서는 s의 구현체 별 정렬 기준에 따라 바뀜
        s.addAll(Arrays.asList(args).subList(1, args.length));
        System.out.println(s);
    }

    private static void fatalError(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
