package chapter5.item31;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

// T 생산자 매개변수에 와일드카드 타입 적용 (184쪽)
public class Chooser<T> {
    private final List<T> choiceList;
    private final Random rnd = new Random();

    // 코드 31-5 T 생산자 매개변수에 와일드카드 타입 적용 (184쪽)
    public Chooser(Collection<? extends T> choices) {
        //chooseList에 값을 생산(추가)하는 것
        //T 타입의 값을 생산하기만 한다.
        //Chooser<Number>에 List<Integer>넘겨줘도 문제 없다.
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3, 4, 5, 6);
        Chooser<Number> chooser = new Chooser<>(intList);
        for (int i = 0; i < 10; i++) {
            Number choice = chooser.choose();
            System.out.println(choice);
        }
    }
}