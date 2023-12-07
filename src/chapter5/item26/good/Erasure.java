package chapter5.item26.good;

public class Erasure<T extends Comparable<T>> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

//이걸 컴파일하면
//public class Test {
//    private Comparable data;
//
//    public Comparable getData() {
//        return data;
//    }
//
//    public void setData(Comparable data) {
//        this.data = data;
//    }
//}