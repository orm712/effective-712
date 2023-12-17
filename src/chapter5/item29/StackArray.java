package chapter5.item29;

import chapter5.item29.EmptyStackException;

import java.util.Arrays;

public class StackArray {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public StackArray() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
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

    public static void main(String[] args) {
        StackArray stack = new StackArray();
        stack.push("a");

        //클라이언트가 직접 형변환 해야함 -> 런타임 오류날 수도 있음
        String temp = (String) stack.pop();

        System.out.println(temp);

//        while (!stack.isEmpty())
//            System.out.println(stack.pop());
    }
}