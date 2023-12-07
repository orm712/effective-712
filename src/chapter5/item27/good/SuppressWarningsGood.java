package chapter5.item27.good;

import java.util.ArrayList;
import java.util.List;

public class SuppressWarningsGood {
    public void myMethod() {
        List rawList = new ArrayList();
        addToList(rawList); // 지역 변수에만 적용
    }

    @SuppressWarnings("unchecked")
    private void addToList(List list) {
        list.add("String value"); // 경고 억제 대상
    }
}
