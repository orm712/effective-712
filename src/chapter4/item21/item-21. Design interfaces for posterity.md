인터페이스는 구현하는 쪽을 생각해 설계하라.
# 인터페이스에 메서드를 추가하는 방법
## Java 8 이전
기존 구현체를 깨뜨리지 않는 이상 인터페이스에 메서드를 추가할 방법이 없었다.
메서드를 추가 한다면 웬만해선 해당 메서드의 이름을 쓰고있는 구현체가 있을 확률이 낮고, 따라서 해당 메서드가 재정의되지 않아 컴파일 시간 오류가 발생한다.
## Java 8 이후
기존 인터페이스에 메서드를 추가할 수 있는 **`디폴트 메서드`** 가 소개되어 상황이 나아지긴 했지만 위험이 완전히 없어진 것은 아니다.
디폴트 메서드를 통해 기존의 인터페이스에 메서드를 추가할 수 있게 되었지만, 모든 기존의 구현체들과 연동된다는 보장은 없다.
Java 7 까지만 해도 '현재 인터페이스에 새로운 메서드가 추가될 것'이라는 가정 없이 작성했기 때문이다.
### Java 8에서의 디폴트 메서드 활용
Java 8에서 핵심 컬렉션 인터페이스에 여러 디폴트 메서드가 추가되었다.
자바 라이브러리에서 작성된 것들은 품질도 높고 범용적이지만, '**생각 가능한 모든 상황에서 불변식을 해치지 않을 디폴트 메서드를 작성한다.**'라는 것은 쉽지 않다.
### 예시 - `removeIf` (from `Collection`)
Java 8에 `Collection` interface에 추가된 `removeIf`는 인수로 주어지는 boolean 함수(predicate)가 true를 반환하는 모든 원소를 제거한다.
디폴트 구현은 iterator를 이용해 순회하며, 각 원소를 predicate의 인수로 넣어 호출한 뒤, predicate가 true를 반환하면 `iterator.remove()`를 호출해 해당 원소를 제거한다.
```java
default boolean removeIf(Predicate filter) {  
    Objects.requireNonNull(filter);  
    boolean result = false;  
    for (Iterator<E> it = iterator(); it.hasNext(); ) {  
        if(filter.test(it.next())) {  
            it.remove();  
            result = true;  
        }  
    }  
    return result;  
}
```
### `removeIf` 와 Collection 구현체의 충돌
`removeIf`가 범용적으로 작성되긴 했지만, 모든 Collection 구현체와 잘 어울리지는 못한다.
그 중 하나인 `org.apache.commons.collections4.collection.SynchronizedCollection`(*모든 메서드에서 주어진 락 객체로 동기화한 후, 내부 컬렉션 객체에 기능을 위임하는 래퍼 클래스*) 의 경우, 책을 작성한 시점에선 `removeIf` 메서드를 재정의하고 있지 않다.
(현재 `ver 4.4`까지 릴리즈 되었으며, `ver 4.4`부터는 지원하고 있다.)
따라서, Java 8과 해당 클래스를 함께 사용한다면 `Collection` 인터페이스의 `removeIf` 디폴트 메서드를 물려 받게되고, 메서드 호출의 동기화 처리를 해주지 못하게 된다. 따라서 `SynchronizedCollection`의 인스턴스를 멀티 스레딩 환경에서 한 스레드가 `removeIf`를 호출하도록 하면 `ConcurrentModificationException`이 발생하거나 에러가 발생할 수 있다.
### Java Platform Library의 대처
이러한 사례를 대처하기 위해, implements한 인터페이스의 디폴트 메서드를 재정의하고 다른 메서드에서는 디폴트 메서드를 호출하기 전, 필요한 작업을 수행하도록 했다.
`Collections.synchronizedCollection`이 반환한 package-private 클래스들은 `removeIf`를 재정의하고, 이들을 호출하는 다른 메서드들은 `removeIf`의 디폴트 구현을 호출하기 전 동기화를 하도록 했다.
하지만 이러한 대처는 **Java Platform에 속한** Collection 구현체들에 한정되어 있고, 제3의 구현체들은 인터페이스의 변화에 따라 수정될 기회가 없었다.
## 디폴트 메서드가 있는 경우 기존 구현체는 오류/경고 없이 컴파일 되지만 런타임에 오류가 발생할 수 있다.
앞선 예와 같이, Java 8에선 Collection 인터페이스에 많은 디폴트 메서드들이 추가되었기 때문에 많은 기존 구현체들이 영향을 받을 수 있다.
따라서, **기존의 인터페이스에 디폴트 메서드를 추가**하는 일은 *웬만해서 피하고*, 꼭 추가해야 한다면 **기존 구현체들과의 충돌 가능성**을 **염두**해야 한다.
반면 **새로운 인터페이스를 만든다면**, 디폴트 메서드는 **표준 메서드를 제공**하는데 아주 유용한 수단이다.
또한, 디폴트 메서드를 인터페이스의 메서드를 지우거나 시그니처를 수정하는 용도로 사용해선 안된다. 이는 기존 클라이언트들을 망가뜨린다.
# 인터페이스를 설계할 때는, 여전히 세심한 주의가 필요하다.
디폴트 메서드가 등장한 현재에도 말이다.
새로운 인터페이스를 릴리즈 할 때도, 서로 다른 방식으로 최소 세 가지의 구현체를 만들어보며 테스트 해보아야 한다. 그리고 그를 활용하는 클라이언트도 여러 개 만들어보아야 한다.
# 인터페이스의 결함은 릴리즈 후에도 수정 가능하지만, 그러한 가능성에 기대어선 안 된다.