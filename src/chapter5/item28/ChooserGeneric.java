package chapter5.item28;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//제네릭을 사용한 버전
public class ChooserGeneric<T> {
    //private final Object[] choiceArray;
    //private final T[] choiceArray;
    private final List<T> choiceList;


//    public Chooser(Collection choices) {
//        choiceArray = choices.toArray();
//    }
    public ChooserGeneric(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3, 4, 5, 6);

        ChooserGeneric<Integer> chooser = new ChooserGeneric<>(intList);

        for (int i = 0; i < 10; i++) {
            //형변환 필요없음 ClassCastException 발생하지 않음
            Number choice = chooser.choose();
            System.out.println(choice);
        }
    }
}