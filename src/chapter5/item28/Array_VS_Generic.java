package chapter5.item28;

import java.util.ArrayList;
import java.util.List;

public class Array_VS_Generic {
    public static void main(String[] args) {
        Object[] objectArray = new Long[1];
        objectArray[0] = "타입이 달라 넣을 수 없다."; // ArrayStoreException 예외 발생

//        List<Object> ol = new ArrayList<Long>(); //호환되지 않는 타입
//        ol.add("타입이 달라 넣을 수 없다."); // 컴파일 에러

        //결국 둘다 String을 넣을 수는 없다.
        //그러나 배열은 런타임에 에러를 발생시키고, 제네릭은 컴파일타임에 에러를 발생시킨다.

        //컴파일 때 먼저 알 수 있으므로 제네릭을 사용하는 것이 좋다.
    }
}
