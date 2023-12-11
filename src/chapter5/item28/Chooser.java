package chapter5.item28;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// 코드 28-6 리스트 기반 Chooser - 타입 안전성 확보! (168쪽)
public class Chooser {
    private final Object[] choiceArray;

    public Chooser(Collection choices) {
        choiceArray = choices.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3, 4, 5, 6);

        Chooser[] chooser = new Chooser[10];
        for (int i = 0; i < 10; i++) {
            chooser[i] = new Chooser(intList);
        }

        for (int i = 0; i < 10; i++) {
            //choose() 메서드를 호출할 때마다 형변환 해줘야함
            //혹시나 다른 타입이 들어가있으면 형변환 오류
            Number choice = (Number) chooser[i].choose();
            System.out.println(choice);
        }
    }
}