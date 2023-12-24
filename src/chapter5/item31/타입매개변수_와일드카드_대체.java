package chapter5.item31;

import java.util.List;

public class 타입매개변수_와일드카드_대체 {
    //비한정적 타입 매개변수
    public static void printList(List<?> list) {
        for (Object element : list) {
            System.out.println(element);
        }
    }
    //비한정적 와일드카드
    public static void printListWild(List<?> list) {
        for (Object element : list) {
            System.out.println(element);
        }
    }


    //한정적 타입 매개변수
    public static <T extends Number> double sum(List<T> list) {
        double sum = 0.0;
        for (T element : list) {
            sum += element.doubleValue();
        }
        return sum;
    }
    //한정적 와일드카드
    public static double sumWild(List<? extends Number> list) {
        double sum = 0.0;
        for (Number element : list) {
            sum += element.doubleValue();
        }
        return sum;
    }

}
