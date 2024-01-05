package chapter5.item31;

import java.util.*;

public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size==0)
            throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

//    // 코드 31-1 와일드카드 타입을 사용하지 않은 pushAll 메서드 - 결함이 있다!
//    // 매개변수화 타입이 불공변이기 때문이다. (Iterable<Number>와 Iterable<Integer>는 하위/상위타입이 아니다.)
//    public void pushAll(Iterable<E> src) {
    //  //타입이 일치하면 괜찮은데 상위/하위타입(Number/Integer)이면 오류 발생
//        for (E e : src)
//            push(e);
//    }

    // 코드 31-2 E 생산자(producer) 매개변수에 와일드카드 타입 적용
    public void pushAll(Iterable<? extends E> src) {
        //E의 Iterable이 아니라 E의 하위타입의 Iterable이어야 함
        //Number에 Integer를 넣을 수 있음
        for (E e : src)
            push(e);
    }

//    // 코드 31-3 와일드카드 타입을 사용하지 않은 popAll 메서드 - 결함이 있다!
//    public void popAll(Collection<E> dst) {
//        while (!isEmpty())
//            dst.add(pop());
//    }

    // 코드 31-4 E 소비자(consumer) 매개변수에 와일드카드 타입 적용
    public void popAll(Collection<? super E> dst) {
        //pop하고 dst에 넣는 함수
        //E의 Collection이 아니라 E의 상위타입의 Collection이어야 함
        //Number에 Object를 넣을 수 있음
        while (!isEmpty())
            dst.add(pop());
    }

    // 제네릭 Stack을 사용하는 맛보기 프로그램
    public static void main(String[] args) {
        Stack<Number> numberStack = new Stack<>();
        Iterable<Integer> integers = Arrays.asList(3, 1, 4, 1, 5, 9);
        numberStack.pushAll(integers);

        Collection<Object> objects = new ArrayList<>();
        numberStack.popAll(objects);

        System.out.println(objects);
    }
}