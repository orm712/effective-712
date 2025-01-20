package chapter4.item15;

import chapter1.item3.readResolveTest;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;

public class SerializableExposeExample {

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
        ObjectOutputStream OOS = new ObjectOutputStream(BAOS);
        OOS.writeObject(new TestClass());
        OOS.close();
        printHexDump(BAOS.toByteArray());
    }
    private static void printHexDump(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            // 오프셋 출력
            if (i % 16 == 0) {
                System.out.printf("\n%04X: ", i);
            }

            // 16진수로 바이트 출력
            System.out.printf("%02X ", bytes[i]);

            // 8바이트마다 추가 공백
            if ((i + 1) % 8 == 0) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}

class TestClass implements Serializable {
    private int x = 20;
    protected int y = 30;
}
