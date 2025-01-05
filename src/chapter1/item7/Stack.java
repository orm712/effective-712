package chapter1.item7;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }
    /**
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }
    **/

    // 개선된 버전의 pop
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조를 해제한다
        return result;
    }
    /**
     * 적어도 하나 이상의 원소를 위한 공간을 확보하기 위한 메서드
     * 배열의 확장이 필요할 때 마다 크기를 2배씩 늘린다.
     */
    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

    public static void main(String[] args) {
        Stack stack = new Stack();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.push(40);
        stack.pop();
        // 활성 영역(elements의 index 0~2) 밖의 원소인 elements[3]에 접근이 가능
        // 즉, 불필요한 참조가 아직 존재
        System.out.println(stack.elements[3]);
    }
}
