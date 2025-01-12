package chapter1.item9;

import java.io.IOException;

public class ResourcesExample {
    public static void main(String[] args) throws Exception{
        // readWithTryFinally();
        readWithTryWithResources();
    }
    public static void readWithTryFinally() throws IOException {
        // try-finally 문을 사용해 자원에 접근하는 예시
        Resource resource = new Resource();
        try {
            resource.read();
        } finally {
            // 마지막에 발생한 예외, 즉 close 메서드에서 발생한 예외만 StackTrace 에 출력되고
            // 앞서 발생한 예외, 즉 read 메서드에서 발생한 예외는 출력되지 않음
            resource.close();
        }
    }
    public static void readWithTryWithResources() throws IOException{
        // try-with-resources 문을 사용해 자원에 접근하는 예시
        // StackTrace 에 read 메서드에서 발생한 예외가 출력되고,
        // close 메서드에서 발생한 예외는 숨겨짐 (Suppressed) 처리되어 출력됨
        try(Resource resource = new Resource()) {
            resource.read();
        }
    }
}

// 자원 예시 클래스
// 읽기 작업 수행시 예외를 던지고
// 읽기 작업이 수행된 경우, 닫기 작업에서도 예외를 던지도록 동작
class Resource implements AutoCloseable{
    private boolean isRead = false;

    public void read() throws IOException {
        isRead = true;
        throw new IOException("failed to read file");
    }
    @Override
    public void close() throws IOException {
        if(isRead) {
            throw new IOException("failed to close resources");
        }
    }
}