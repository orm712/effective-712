package chapter12.item85;// 간단한 예시 코드

import java.io.*;

public class ObjectInputFilterExample {

    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
    public static class WrongPerson implements Serializable{
        private String name;
        public WrongPerson(String name) {
            this.name = name;
        }
        public String toString() {
            return "WrongPerson{name='" + name + "}";
        }
    }
    public static void main(String[] args) {
        // 직렬화
        Person person = new Person("Alice", 30);
        WrongPerson wrongPerson = new WrongPerson("ABCDEF");
        byte[] data = serialize(person);
        byte[] wrongData = serialize(wrongPerson);

        System.out.println(Person.class.getName());
        // 역직렬화
        deserialize(data);
        // 예외값 역직렬화
        deserialize(wrongData);
    }

    // 직렬화 메서드
    private static byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(obj);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    // 역직렬화 메서드
    private static void deserialize(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            // ObjectInputFilter 설정: Person 클래스만 허용
            // createFilter 메서드는 ;로 구분되는 문자열 패턴을 매개변수로 받음
            // maxdepth=value, maxbytes=value과 같은 속성을 설정할 수도 있고
            // 아래 예시 처럼 클래스 이름을 설정해, 이와 일치하는 클래스는
            // 허용하거나 거부할 수 있음
            // 1. 패턴이 !로 시작할 경우 나머지 패턴이 일치할 때 클래스가 거부되며,
            // !로 시작하지 않을 경우 패턴이 일치하는 클래스들이 허용됨
            // 2. 패턴이 *로 끝나면 해당 패턴을 접두사로 쓰는 모든 클래스와 일치함
            // 즉, 아래 패턴은 Class.getName()에서 반환되는 클래스 또는 패키지 이름이 "Person"과 일치하는 경우 허용하고,
            // 그 외의 패턴은 전부 거부하는 형태
            // 정확히는, FQCN(Fully Qualified Class Name)으로 "패키지명을 포함한 클래스 이름"을 입력해야 함
            // 또한, !*를 통해 필터링하지만, String과 같이 일부 자바 시스템 클래스는 예외적으로 통과함
            ObjectInputFilter filter = ObjectInputFilter.Config.createFilter("chapter12.item85.ObjectInputFilterExample$Person;!*");
            ois.setObjectInputFilter(filter);

            Object obj = ois.readObject();
            System.out.println("Deserialized object: " + obj);

        } catch (InvalidClassException e) {
            System.err.println("Invalid class during deserialization: " + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Deserialization error: " + e.getMessage());
        }
    }
}