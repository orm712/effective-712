정의하려는 것이 타입이라면, `마커 인터페이스`를 사용하라.
## `마커 인터페이스`?
`마커 인터페이스(marker interface)`란, *아무 메서드도 담고 있지 않고* 단지 자신을 **implement하는 클래스**가 '**특정 속성(property)을 가짐**'을 **표시**해주는 인터페이스를 말한다.
### 예시 - `Serializable`
`Serializable` 인터페이스는 이를 implement한 클래스의 인스턴스는 `ObjectOutputStream`에 **쓰일 수(write) 있다**고 알려주는 `마커 인터페이스`입니다.
## `마커 애너테이션(item 39)`와의 비교
마커 애너테이션의 등장으로, 마커 인터페이스가 구식이 되었다는 얘기가 있지만 실제로는 `마커 인터페이스`가 *두 가지 측면*에서 **더 나은 점**을 갖고 있다.
### 1. `마커 인터페이스`는 implement한 클래스의 인스턴스를 **구분하는 타입**으로 쓸 수 있다.
이를 통해, 마커 애너테이션은 런타임에서나 잡을 수 있는 오류를 컴파일타임에 잡을 수 있다.  
#### 예시
`ObjectOutputStream`의 `writeObject` 메서드는 인수로 들어올 객체가 `Serializable`을 implement 했을 것이라 가정하지만, *실제로는* `Object` 객체를 받도록 설계 되어있다.
```java
public final void writeObject(Object obj) throws IOException {  
	...
}
```
따라서, 인수 타입을 `Serializable`로 했다면 챙길 수 있었던 컴파일시간에 오류를 검출할 수 있다는 *이점을 살리지 못한 케이스*이다.
### 2. 대상을 더 정밀하게 지정할 수 있다.
`@Target(ElementType.TYPE)`으로 선언한 애너테이션은 모든 타입(`class`, `interface`, `enum`, `annotation`)에 달 수 있다. 이보다 더 세밀하게는 제한할 수 없다.  
만약 *특정 인터페이스를 implements 한 클래스*에만 **Marker를 적용**하고 싶다면?  
그냥 이 Marker를 `마커 인터페이스`로 정의하면 된다! 그리고 마킹하고픈 클래스들이 이 `마커 인터페이스`를 implements 하도록 하면 된다.  
그렇게 하면 마킹된 요소들은 모두 `마커 인터페이스`의 하위 타입임이 보장된다.

반대로, `마커 애너테이션`이 더 나은점도 있다!
### 1. 거대한 애너테이션 시스템의 구성 요소이다.
따라서, **애너테이션을 주로 활용하는 프레임워크**에서는 **일관성**을 지키는데 효과적이다.

## `Set`도 `마커 인터페이스`?
이견이 있을 수 있지만, Set 인터페이스 역시 일종의 `마커 인터페이스`로 간주될 수 있다.  
이유는 다음과 같다.
1. `Set`은 `Collection`의 **하위 타입에만** 적용할 수 있다.
2. `Collection`이 *기존에 정의한 메서드* 외에 **추가된 것이 없다**

| Set | Collection |
| --- | ---------- |
| boolean	add(E e)<br>boolean	addAll(Collection<? extends E> c)<br>void	clear()<br>boolean	contains(Object o)<br>boolean	containsAll(Collection<?> c)<br>boolean	equals(Object o)<br>int	hashCode()<br>boolean	isEmpty()<br>Iterator<E>	iterator()<br>boolean	remove(Object o)<br>boolean	removeAll(Collection<?> c)<br>boolean	retainAll(Collection<?> c)<br>int	size()<br>default Spliterator<E>	spliterator()<br>Object[]	toArray()<br><T> T[]	toArray(T[] a) |    boolean	add(E e)<br>boolean	addAll(Collection<? extends E> c)<br>void	clear()<br>boolean	contains(Object o)<br>boolean	containsAll(Collection<?> c)<br>boolean	equals(Object o)<br>int	hashCode()<br>boolean	isEmpty()<br>Iterator<E>	iterator()<br><mark style="background: #FFB86CA6;">default Stream<E>	parallelStream()</mark><br>boolean	remove(Object o)<br>boolean	removeAll(Collection<?> c)<br><mark style="background: #FFB86CA6;">default boolean	removeIf(Predicate<? super E> filter)</mark><br>boolean	retainAll(Collection<?> c)<br>int	size()<br>default Spliterator<E>	spliterator()<br><mark style="background: #FFB86CA6;">default Stream<E>	stream()</mark><br>Object[]	toArray()<br><T> T[]	toArray(T[] a) |
물론 `add`, `equals`, `hashCode` 등 `Collection`의 *메서드 규약을 수정*했기 때문에 마커 인터페이스가 아니라 생각할 수 있다.
하지만, '①*특정 인터페이스의 하위 타입에만* 적용 가능하며, ②인터페이스의 **메서드 규약을 손대지 않은**' `마커 인터페이스`는 충분히 있을만하다.  
그리고 이러한 `마커 인터페이스`는 다음과 같은 용도로 사용할 수 있을 것이다.
1. 전체 객체의 일부 `불변성(invariant)`을 설명한다.
2. 해당 타입의 인스턴스가 *다른 클래스의 특정 메서드*가 처리할 수 있다는 점을 명시한다.
## 마커 인터페이스, 또는 마커 애너테이션을 써야하는 상황
### `마커 애너테이션`을 써야할 때
1. *클래스, 인터페이스를 제외*한 요소(module, package, field, local variable...)에 마킹해야 할 때
	- 마커 인터페이스는 클래스/인터페이스만이 `implements`/`extends` 할 수 있기 때문이다.
2. *애너테이션을 자주 쓰는* 프레임워크에서 마킹하는 경우
### `마커 인터페이스`를 써야 할 때
1. **마킹을 적용한 객체**를 *인수로 받는 메서드*를 써야할 때
	- `마커 인터페이스`를 적용해 컴파일시간에 오류를 잡을 수 있다.
	
# 핵심 정리
- 마커 인터페이스, 그리고 마커 애너테이션은 각자 쓰임이 있다.
	- *메서드 추가 없이* **타입 정의하는게 목적**이라면 <mark style="background: #ADCCFFA6;">마커 인터페이스</mark>를 선택하자.
	- 클래스/인터페이스 외의 요소에 마킹하거나, 애너테이션을 적극 활용하는 프레임워크에서는 <mark style="background: #FFB86CA6;">마커 애너테이션</mark>을 선택하자.
- 적용 대상이 `ElementType.TYPE`인 마커 애너테이션을 작성중이라면, *애너테이션*으로 작성하는게 맞는지 **다시 생각**해보자.
- 이번 item은 '**타입 정의**할거면, **인터페이스**를 써라' 라고 해석할 수 있다.
	- 따라서, `item 22`('타입 정의할 것 아니라면, 인터페이스 쓰지 마라')를 뒤집은 것으로 볼 수 있다.