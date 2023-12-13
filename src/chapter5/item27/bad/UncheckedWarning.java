package chapter5.item27.bad;

import java.util.HashSet;
import java.util.Set;

public class UncheckedWarning {
    public static void main(String[] args) {
        //Shift두번 누르고 Inspect Code
        Set<String> exaltation = new HashSet<>();

        exaltation.add("1");
    }
}
