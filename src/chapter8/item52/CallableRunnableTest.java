package chapter8.item52;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallableRunnableTest {
    public static void main(String[] args) {
        Thread t = new Thread(System.out::println);

        ExecutorService exec = Executors.newCachedThreadPool();

        // java: reference to submit is ambiguous
        //  both method <T>submit(java.util.concurrent.Callable<T>) in java.util.concurrent.ExecutorService and
        //  method submit(java.lang.Runnable) in java.util.concurrent.ExecutorService match

        // 위 오류가 발생하고, Java에서는 Callable로 따져보려 했으나, println의 반환값 void가 T에 지정될 수 없어 실패
        // java: incompatible types: cannot infer type-variable(s) T
        //         (argument mismatch; bad return type in method reference
        // void cannot be converted to T)
//         exec.submit(System.out::println);
    }
}
