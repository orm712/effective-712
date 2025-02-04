package chapter4.item18;

import java.util.Collection;
import java.util.HashSet;

public class InstrumentedHashSet<E> extends HashSet<E> {
    // 요소 삽입을 시도한 횟수
    private int addCount = 0;
    public InstrumentedHashSet() {
    }

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    @Override public boolean addAll(Collection<? extends E> c) {
        // 오류 발생
        // 실제로 super.addAll() (HashSet 의 addAll)은 내부적으로 add를 사용함
        // 따라서 c.size()를 추가한 뒤, c의 원소 갯수 만큼 addCount++가 중복적으로 수행됨
        addCount += c.size();
        return super.addAll(c);
    }
    public int getAddCount() {
        return addCount;
    }
}