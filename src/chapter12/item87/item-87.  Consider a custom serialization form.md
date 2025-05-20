# 커스텀 직렬화 형태를 고려해보라
## 왜 커스텀 직렬화를 고려해야 할까?
> - 클래스가 `Serializable`을 구현하고 기본 직렬화 형태를 사용한다면 현재의 구현에 종속적이게 된다.
> - 즉, 기본 직렬화 형태를 버릴 수 없게 된다.
> - 따라서 유연성, 성능, 정확성과 같은 측면을 고민한 후에 합당하다고 생각되면 기본 직렬화 형태를 사용해야 한다.

## 이상적인 직렬화 형태
> - 기본 직렬화 형태는 객체가 포함한 데이터뿐만 아니라 그 객체를 시작으로 접근할 수 있는 모든 객체와 객체들의 연결된 정보까지 나타낸다. 
> - 이상적인 직렬화 형태라면 물리적인 모습과 독립된 논리적인 모습만을 표현해야 한다. 
> - 객체의 물리적 표현과 논리적 내용이 같다면 기본 직렬화 형태를 선택해도 무방하다. 
>   - 예를 들어 사람의 이름을 표현하는 클래스는 괜찮을 것이다.
```java
public class Name implements Serializable {
    /**
     * 성. null이 아니어야 한다.
     * @serial
     */
    private final Stirng lastName;

    /**
     * 이름. null이 아니어야 한다.
     * @serial
     */
    private final String firstName;

    /**
     * 중간이름. 중간이름이 없다면 null
     * @serial
     */
    private final String middleName;

    ... // 나머지 코드는 생략
}
```
> - 이름은 논리적으로 성, 이름, 중간 이름이라는 3개의 문자열로 구성하는데 위 클래스의 인스턴스 필드들은 이 논리적인 구성 요소를 정확하게 반영했다.

> - 기본 직렬화 형태가 적합해도 불변식 보장과 보안을 위해 readObject 메서드를 제공해야 하는 경우가 많다. 
> - 앞에서 살펴본 Name 클래스를 예로 들면, lastName과 firstName 필드가 null이 아님을 readObject 메서드가 보장해야 한다.
>   - readObject : 스트림에서 필드 정보와 기타 데이터를 읽어와 객체를 복원
>     - `ObjectInputStream.readObject()`로 전체 객체를 읽을 때, Java 직렬화 런타임이 리플렉션(reflection)을 통해 자동으로 호출

## 기본 직렬화 형태에 적합하지 않은 경우
```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }
    // ... 생략
}
```
> - 위 클래스는 논리적으로 문자열을 표현했고 물리적으로는 문자열들을 이중 연결 리스트로 표현했다. 
>   - 이 클래스에 기본 직렬화 형태를 사용하면 각 노드에 연결된 노드들까지 모두 표현할 것이다. 따라서 객체의 물리적 표현과 논리적 표현의 차이가 클 때는 아래와 같은 문제가 생긴다.

> - 공개 API가 현재의 내부 표현 방식에 종속적이게 된다.
>   - 예를 들어, 향후 버전에서는 연결 리스트를 사용하지 않게 바꾸더라도 관련 처리는 필요해진다. 따라서 코드를 절대 제거할 수 없다.
> - 사이즈가 크다.
>   - 위의 StringList 클래스를 예로 들면, 기본 직렬화를 사용할 때 각 노드의 연결 정보까지 모두 포함될 것이다.
>   - 이런 정보는 내부 구현에 해당하니 직렬화 형태에 가치가 없다. 오히려 네트워크로 전송하는 속도를 느리게 한다.
> - 시간이 많이 걸린다.
>   - 직렬화 로직은 객체 그래프의 위상에 관한 정보를 알 수 없으니, 직접 순회할 수밖에 없다.
> - 스택 오버플로를 발생시킨다.
>   - 기본 직렬화 형태는 객체 그래프를 재귀 순회한다. 호출 정도가 많아지면 이를 위한 스택이 감당하지 못할 것이다.

## 합리적인 직렬화 형태
> - 단순히 리스트가 포함한 문자열의 개수와 문자열들만 있으면 될 것이다.
>   - 물리적인 상세 표현은 배제하고 논리적인 구성만을 담으면 된다.
```java
public final class StringList implements Serializable {
    private transient int size = 0;
    private transient Entry head = null;

    // 이번에는 직렬화 하지 않는다.
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    // 문자열을 리스트에 추가한다.
    public final void add(String s) { ... }

    /**
     * StringList 인스턴스를 직렬화한다.
     */
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(size);

        // 모든 원소를 순서대로 기록한다.
        for (Entry e = head; e != null; e = e.next) {
            stream.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int numElements = stream.readInt();

        for (int i = 0; i < numElements; i++) {
            add((String) stream.readObject());
        }
    }
    // ... 생략
}
```
> - transient 키워드가 붙은 필드는 기본 직렬화 형태에 포함되지 않는다.
>   - 클래스의 모든 필드가 transient로 선언되었더라도 writeObject와 readObject 메서드는 각각 defaultWriteObject와 defaultReadObject 메서드를 호출한다.
>     - 이렇게 해야 향후 릴리즈에서 transient가 아닌 필드가 추가되더라도 상위와 하위 모두 호환이 가능하다.
>     - 새롭게 추가되는 애들만 stream에 넣어서 직렬화
> - 신버전의 인스턴스를 직렬화한 후에 구버전으로 역직렬화하면 새로 추가된 필드는 무시될 것이다.
>   - 또 구버전 readObject 메서드에서 defaultReadObject를 호출하지 않는다면 역직렬화 과정에서 StreamCorruptedException이 발생한다.

## 고려할 점
> - 기본 직렬화 여부에 관계없이 defaultWriteObject 메서드를 호출하면 transient로 선언하지 않은 모든 필드는 직렬화된다.
>   - 논리적 상태와 무관한 필드라고 판단될 때만 생략하면 된다.
> - 기본 직렬화를 사용한다면 역직렬화할 때 transient 필드는 기본값으로 초기화된다.
>   - 기본값을 변경해야 하는 경우에는 readObject 메서드에서 defaultReadObject 메서드를 호출한 다음 원하는 값으로 지정하면 된다.
>   - 아니면 그 값을 처음 사용할 때 초기화해도 된다.

## 동기화
> - 기본 직렬화 사용 여부와 상관없이 직렬화에도 동기화 규칙을 적용해야 한다.
>   - 예를 들어 모든 메서드를 synchronized로 선언하여 스레드 안전하게 만든 객체에 기본 직렬화를 사용한다면, writeObject도 아래처럼 수정해야 한다.
```java
private synchronized void writeObject(ObjectOutputStream stream)
        throws IOException {
    stream.defaultWriteObject();
}
```

## SerialVersionUID
> - 어떤 직렬화 형태를 선택하더라도 직렬화가 가능한 클래스에는 SerialVersionUID(이하 SUID)를 명시적으로 선언해야 한다
>   - 물론 선언하지 않으면 자동 생성되지만 런타임에 이 값을 생성하느라 복잡한 연산을 수행해야 한다.
```java
private static final long serialVersionUID = 0204L;
```
