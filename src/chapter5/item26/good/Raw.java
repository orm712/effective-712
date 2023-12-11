package chapter5.item26.good;
import java.util.*;

public class Raw {

    private static final Collection<Stamp> stamps = new ArrayList<Stamp>();

    public static void main(String[] args) {
//
//        stamps.add(new Coin(1)); // 바로 오류 뜸


        for(Iterator i = stamps.iterator(); i.hasNext();){
            //꺼낼 때 오류 걸림
            Stamp stamp = (Stamp) i.next();
        }
    }

    private static class Stamp{
        int a;
        Stamp(int a){
            this.a = a;
        }
    }
    private static class Coin{
        int b;
        Coin(int b){
            this.b = b;
        }
    }
}
