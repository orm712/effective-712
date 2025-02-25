package chapter8.item56;

/**
 * class for Test JavaDoc
 *
 */
public class JavadocTest implements interface1 {
    /**
     * {@inheritDoc}
     */
    @Override
    public void doSomething (int value) {
        value += 20;
        System.out.println(value);
    }
}

interface interface1 {
    /**
     * Method for Test JavaDoc
     * @implSpec
     * this implementation print {@code vlaue + 20}
     *
     * @param value 아무런 int 값을 받아 사용한다
     * @throws NullPointerException 그냥 unchecked exception 임
     */
    public void doSomething (int value);
}