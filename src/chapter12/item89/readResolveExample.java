package chapter12.item89;

import java.io.*;

public class readResolveExample {
    // 진짜 Elvis 인스턴스로는 만들어질 수 없는 임의의 바이트 스트림
    // 해당 바이트 스트림은 Elvis와 ElvisStealer에 대한 설명,
    // 그리고 ElvisStealer.payload에 (직렬화된) Elvis 인스턴스를 가리키도록
    // 악의적으로 설정해놓은 바이트 스트림임
    private static final byte[] serializedForm = {
            (byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x05,
            0x45, 0x6c, 0x76, 0x69, 0x73, (byte)0x84, (byte)0xe6,
            (byte)0x93, 0x33, (byte)0xc3, (byte)0xf4, (byte)0x8b,
            0x32, 0x02, 0x00, 0x01, 0x4c, 0x00, 0x0d, 0x66, 0x61, 0x76,
            0x6f, 0x72, 0x69, 0x74, 0x65, 0x53, 0x6f, 0x6e, 0x67, 0x73,
            0x74, 0x00, 0x12, 0x4c, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c,
            0x61, 0x6e, 0x67, 0x2f, 0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74,
            0x3b, 0x78, 0x70, 0x73, 0x72, 0x00, 0x0c, 0x45, 0x6c, 0x76,
            0x69, 0x73, 0x53, 0x74, 0x65, 0x61, 0x6c, 0x65, 0x72, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,
            0x4c, 0x00, 0x07, 0x70, 0x61, 0x79, 0x6c, 0x6f, 0x61, 0x64,
            0x74, 0x00, 0x07, 0x4c, 0x45, 0x6c, 0x76, 0x69, 0x73, 0x3b,
            0x78, 0x70, 0x71, 0x00, 0x7e, 0x00, 0x02
    };
    static Object deserialize(byte[] sf) {
        try {
            return new ObjectInputStream(
                    new ByteArrayInputStream(sf)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }

    }
    public static void main(String[] args) throws IOException{
        // ElvisStealer.impersonator를 초기화 한 뒤,
        // 진짜 Elvis(즉, `Elvis.INSTANCE`)를 반환

        // 1. Elvis deserialize -> Elvis에 포함된 ElvisStealer의 역직렬화 먼저 진행
        // 2. ElvisStealer의 readResolve가 호출되며, 부분적으로 역직렬화되고 readResolve 호출 전인 Elvis가
        // ElvisStealer.payload로 연결됨
        // 3. 그리고 `impersonator = payload` 라인이 호출되면서, 정적 필드인 impersonator로 복사됨
        // 이를 통해 readResolve가 끝난 이후에도 해당 인스턴스를 참조할 수 있음
        // 따라서, 원래 싱글턴이였어야 할 Elvis는
        // 1. 역직렬화된 Elvis,
        // 2. 역직렬화 과정 중간에 낚아챈 Elvis (ElvisStealer.impersonator)
        // 두 개의 인스턴스가 만들어져버림
        Elvis elvis = (Elvis) deserialize(serializedForm);
        Elvis impersonator = ElvisStealer.impersonator;
        elvis.printFavorites();
        impersonator.printFavorites();
        System.out.println(elvis == impersonator);
    }
}
