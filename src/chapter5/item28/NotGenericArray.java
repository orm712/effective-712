package chapter5.item28;

import java.util.List;

public class NotGenericArray {
    public static void main(String[] args) {
//        List<String>[] stringLists = new List<String>[1]; // (1)
//        List<Integer> intList = List.of(42); // (2)
//        Object[] objects = stringLists; // (3)
//        objects[0] = intList; // (4)
//        String s = stringLists[0].get(0); // (5)

        //(1)이 허용된다면
        //(2)는 원소가 하나인 List<Integer>를 생성한다.
        //(3)은 (1)에서 생성한 List<String>의 배열을 Object 배열에 할당한다. 배열이 공변이기 때문에 가능
        //(4)는 (2)에서 생성한 List<Integer>의 인스턴스를 Object 배열의 첫 원소로 저장한다.
        //intList인 List<Integer>는 List가 되고, objects[]인 List<Integer>[]는 List<Integer>로 소거되기 때문에 가능
        //(5)에서 List<String>으로 선언했던 stringLists에 List<Integer>를 넣었기 때문에 ClassCastException이 발생한다.

        //이런 일을 방지해야함
    }
}
