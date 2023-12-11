package chapter5.item27.bad;

import java.util.ArrayList;
import java.util.List;

public class SuppressWarningsBad {
    public void myMethod() {
        @SuppressWarnings("unchecked")
        List rawList = new ArrayList();
        rawList.add("String value"); // 이 부분에서 unchecked 경고 발생

    }
}
