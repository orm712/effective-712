package chapter8.item52;

import java.util.List;

class Wine {
    String name() { return "wine"; }
}
class SparklingWine extends Wine {
    @Override String name() { return "sparkling wine"; }
}
class Champagne extends SparklingWine {
    @Override String name() { return "champagne"; }
}

public class Overriding {
    public static void main(String[] args) {
        List<Wine> wineList = List.of(
                new Wine(), new SparklingWine(), new Champagne());
        // 런타임 타입에 따라 다른 이름들이 출력됨
        for (Wine wine : wineList)
            System.out.println(wine.name());
    }
}