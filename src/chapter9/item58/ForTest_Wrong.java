package chapter9.item58;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ForTest_Wrong {
    enum Suit { CLUB, DIAMOND, HEART, SPADE }
    enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

    public static void main(String[] args) {
        Collection<Suit> suits = Arrays.asList(Suit.values());
        Collection<Rank> ranks = Arrays.asList(Rank.values());


        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();) {
                System.out.println(i.next() + ", " + j.next());
            }
        }
    }
}
