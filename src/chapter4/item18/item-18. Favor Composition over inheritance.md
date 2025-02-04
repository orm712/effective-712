상속보다는 컴포지션을 사용하라.
## 상속
- 상-하위 클래스가 *같은 개발자가 관리하는* `패키지에 속하거나`, `확장을 목적`으로 하여 `문서화가 잘 되어있는 클래스`는 상속에 있어 안전
	- 하지만, `다른 패키지`의 `구체 클래스`를 `상속하는 것은 위험`
### 상속의 문제점
#### 캡슐화 위반
- *상위 클래스의 구현이 변경*되면, `하위 클래스 동작`에 `이상`이 생길 수 있음
	- 만약 `상위 클래스`가 `확장을 고려`하고, `문서화도 잘 된` 경우가 **아니라면**, 하위 클래스는 *상위 클래스의 변화에 따라* `수정`되어야 함
##### 예시 - HashSet
```java
package chapter4.item18;

import java.util.Collection;
import java.util.HashSet;

public class InstrumentedHashSet<E> extends HashSet<E> {
    // 요소 삽입을 시도한 횟수
    private int addCount = 0;
	...
    @Override public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    @Override public boolean addAll(Collection<? extends E> c) {
        // 오류 발생
        // 실제로 super.addAll() (HashSet 의 addAll)은 내부적으로 add를 사용함
        // 따라서 c.size()를 추가한 뒤, c의 원소 갯수 만큼 addCount++가 중복적으로 수행됨
        addCount += c.size();
        return super.addAll(c);
    }
    public int getAddCount() {
        return addCount;
    }
}
```
- *`HashSet`을 상속하되*, 최적화를 위해 *Set 생성 이후* `얼마나 많은 요소들이 추가되었는지 기록`(`addCount`라는 변수에)하고자 함
- 따라서 HashSet의 `add` 와 `addAll`을 재정의하여, `super.add()/addAll()`호출 전 `addCount`를 추가하도록 구현
	- 근데, 실제로 동작시켜보면 `addAll` 호출시 예상과 다르게 `addCount`가 2배로 기록됨
	- 이는 `HashSet`의 `addAll`이 내부에서 각 원소를 `add`를 호출해 추가하도록 동작하기 때문
- 이처럼, *클래스를 구성하는 한 부분에서* `다른 부분을 사용`하는 `자기사용(self-use)`으로 내부 구현이 이뤄진 경우 *다른 클래스들*은 이를 `예측`해서 코드를 구현해야 함
	- 만약 *추후 릴리즈에서* `내부 구현이 변경`될 경우, 이를 사용하던 코드들이 깨지기 쉬움
- `상위 클래스 메서드를 재정의`하는, 즉 *addAll 메서드를 다른 방식으로 재정의* 할 수도 있지만 이 역시 정답은 아님
	- 재정의 하는게 `어렵고`, `시간도 많이 소요`되고, `성능이 떨어질 수도` 있음
	- 또한, *상위 클래스의 `private`/`package-private` 변수를 써야한다면* 이 방법은 **불가능**
##### 또 다른 메서드 재정의 문제
- `컬렉션의 원소`가 모두 *특정 조건을 만족*해야 하는 경우를 가정
	- *해당 컬렉션을 상속*해, `모든 원소 추가 메서드`를 재정의하여 `조건을 검사`하도록 한다면?
	- 상위 클래스에 `또 다른 원소 추가 메서드`가 추가될 경우, 하위 클래스는 해당 메서드를 추가적으로 재정의해주어야 함
		- 만약 그렇게 하지 못하면, "`또 다른 원소 추가 메서드`"를 통해 조건에 허용되지 않는 원소를 추가할 수 있게 됨
##### 새로운 메서드 추가?
- 위 두 가지 문제를 일으키는 `메서드 재정의` 대신, `새로운 메서드를 추가`할 수도 있음
	- 하지만 이는 *추후 상위 클래스에 추가된 메서드*와 `이름이 겹칠 수도` 있음
		- 이 경우, *시그니처는 같으나* `반환 타입이 다르다면` **`컴파일 오류`** 가 `발생`
		- 만약 둘 다 같다면, `메서드 재정의`가 되어버림
## 대안 - 컴포지션
- 새 클래스에 `private 필드`로 `기존 클래스의 인스턴스`를 `참조`하도록 하는 형태
- `새 클래스의 인스턴스 메서드`들은 `기존 클래스 인스턴스`의 `대응되는 메서드`들을 호출해 결과를 반환함
	- 이러한 형태를 `전달(Forwarding)`이라고 하며, `ForwardingSet`의 모든 메서드들을 `전달 메서드(Forwarding Method)`라고 함
	- `컴포지션`과 `전달`의 조합은 *넓은 의미로* `위임(Delegation)`이라고 부름
- `새 클래스`는 *기존 클래스의* `내부 구현의 영향`에서 `벗어나며`, 기존 클래스에 *`새 메서드`가 추가되어도* `영향 없음`
### 예시 코드 - 래퍼 클래스
```java
// 래퍼 클래스 - 상속 대신 컴포지션을 사용
public class InstrumentedSet<E> extends ForwardingSet<E> {
	private int addCount = 0;

	public InstrumentedSet(Set<E> s) {
		super(s);
	}
	@Override 
	public boolean add(E e) {
		addCount++;
		return super.add(e);
	} 
	@Override 
	public boolean addAll(Collection<? extends E> c) {
		addCount += c.size();
		return super.addAll(c);
	} 
	public int getAddCount() {
		return addCount;
	}
}

// 재사용 가능한 전달(Forwarding) 클래스
// 전달 메서드들로만 이뤄짐
public class ForwardingSet<E> implements Set<E> {
	// 실제로 작업을 수행할 Set의 인스턴스인 s를 내부에 가짐
	private final Set<E> s;

	// Set 인스턴스를 인수로 받는 생성자
	public ForwardingSet(Set<E> s) { this.s = s; }
	
	// 아래 메서드들은 모두 실제 작업을 s에게 위임하고 있음
	public void clear() { s.clear(); }
	public boolean contains(Object o) { return s.contains(o); }
	public boolean isEmpty() { return s.isEmpty(); }
	public int size() { return s.size(); }
	public Iterator<E> iterator() { return s.iterator(); }
	public boolean add(E e) { return s.add(e); }
	public boolean remove(Object o) { return s.remove(o); }
	public boolean containsAll(Collection<?> c)
	{ return s.containsAll(c); }
	public boolean addAll(Collection<? extends E> c)
	{ return s.addAll(c); }
	public boolean removeAll(Collection<?> c)
	{ return s.removeAll(c); }
	public boolean retainAll(Collection<?> c)
	{ return s.retainAll(c); }
	public Object[] toArray() { return s.toArray(); }
	public <T> T[] toArray(T[] a) { return s.toArray(a); }
	@Override public boolean equals(Object o)
	{ return s.equals(o); }
	@Override public int hashCode() { return s.hashCode(); }
	@Override public String toString() { return s.toString(); }
}
```
- 임의의 `Set`에 (`addCount`를 `증가`시키는)`계측 기능`을 덧씌운 새로운 `Set`(`InstrumentedSet`)을 만듦
	- 이처럼 `기능을 덧씌우는 형태`를 **`데코레이터 패턴(Decorator Pattern)`** 이라고 부름
- 만약 `상속 방식`을 사용했다면, `Set의 구현체` **각각**을 따로 확장해야하며 `상위 클래스의 각 생성자`에 `대응`되는 `생성자들`을 별도로 정의해주어야 함
	- 가령 `상위 클래스`가 `HashSet`이라면 `HashSet()`, `HashSet(Collection<? extends E> c)` 등의 생성자들 각각에 대응되는 생성자들(`InstrumentedSet()`, `InstrumentedSet(Collection<? extends E> c)`)을 정의해줘야 함
```java
	// TreeSet, HashSet 등 모든 Set 구현체 사용 가능
	Set<Instant> times = new InstrumentedSet<>(new TreeSet<>(cmp));
	Set<E> s = new InstrumentedSet<>(new HashSet<>(INIT_CAPACITY));
```
- 반면, `컴포지션`을 사용하면 `어떠한 Set 구현체`라도 사용할 수 있고, `기존의 모든 생성자들`과 `함께 사용`할 수 있음
```java
static void walk(Set<Dog> dogs) {
	InstrumentedSet<Dog> iDogs = new InstrumentedSet<>(dogs);
	... // 현재 이 메서드에서는 iDogs 인스턴스를 사용(dogs 대신)
	// dogs는 원래 계측되고 있지 않던 Set
}
```
- 또한, `InstrumentedSet`을 쓴다면, (*원래 계측되지 않던 Set 인스턴스를*) `일시적으로 계측`하는 것도 가능
#### 래퍼 클래스의 단점
##### 콜백 프레임워크와 어울리지 않음
- `콜백 프레임워크`는 *자기 자신의 참조를 다른 객체에 넘겨*, `다음 호출(콜백)`때 사용하는 방식으로 동작하는 프레임워크
	- `래팽된 내부 객체`(위 예시에서 `s`)는 `래퍼 클래스`(위 예시에서 `ForwardingSet`)의 존재를 모르므로, `자신(this)`를 넘기게 됨
	- 따라서 `다음 호출`때는 `래퍼 클래스`가 아닌, `내부 객체`를 사용하게 됨
		- 이러한 문제를 `SELF 문제`라고 함
##### 성능 문제?
- `전달 메서드 호출`이나, 래퍼 객체가 `메모리를 차지`해 성능에 대한 영향을 걱정 할 수도 있으나, 미미하다고 함
### 상속 시 유의사항
#### 하위 클래스가 상위 클래스의 "진짜" 하위 타입일 때만 쓸 것
- 클래스 A, B가 있을 때, B가 A의 `is-a 관계` 일때만 `A를 상속`해야 함
- 만약 아니라면, B는 A를 `private 인스턴스`로 두고 `다른 API를 노출`해야 함
	- 즉, A는 B의 *`필수 구성 요소`가 아닌*, `구현의 세부사항`일 뿐임
- 이를 어긴 예시가 `Java Platform Library`의 [Vector](https://docs.oracle.com/javase/8/docs/api/java/util/Vector.html)와 [Stack](https://docs.oracle.com/javase/8/docs/api/java/util/Stack.html), [Hashtable](https://docs.oracle.com/javase/8/docs/api/java/util/Hashtable.html)과 [Properties](https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html)
	- `Stack`은 `Vector`가 아니기 때문에 상속해서는 안됐음
	- 또한, `Properties` 역시 해시테이블이 아니므로 `Hashtable`을 상속해서는 안됐음
		- 이는 `사용자의 혼란을 야기`하는 예시 중 하난데, `Properties`의 인스턴스인 `p`가 있다고 가정했을 때 `p.getProperty(key)`와 `p.get(key)`의 결과가 다를 수 있음
			- `getProperty()`는 `Properties`의 기본 동작이지만, `get()`은 `Hashtable`로부터 상속받은 메서드이기 때문
		- 또한 클라이언트가 `상위 클래스`를 `직접 수정`해, `하위 클래스의 불변성`을 **`손상`** 시킬 수 있음
			- 가령 `Properties`는 `키/값`으로 `문자열`만 허용했으나, `Hashtable`은 그렇지 않아 `Hashtable`의 메서드를 호출하면 `불변식이 깨질 수 있음`
#### 확장을 고려하고 있는 클래스의 API에 결함이 있는지 고려할 것
- 만약 `확장을 고려하고 있는 클래스`(A)의 `API에 결함`이 있다면 이는 `상속하는 클래스(B, 우리가 작성하고자 하는 클래스)의 API`까지 전파됨
	- 만약 `컴포지션`을 사용한다면, *결함을 숨긴* `새로운 API`를 작성할 수 있지만, `상속`은 결함까지도 상속함
# 핵심 정리
- `상속`은 *강력하나*, `캡슐화를 해침`
	- 상위 - 하위 클래스간 관계가 `순수한 is-a 관계` 일때만 사용해야 함
		- 다만, `순수한 is-a 관계`일때도, *두 클래스의 `패키지`가 다르고* ,상위 클래스가 `확장을 고려하지 않은 설계`를 갖고있다면 문제가 됨 
- `컴포지션`과 `전달`은 `상속이 갖는 취약점`에 대한 훌륭한 `대체재`
	- 특히, `래퍼 클래스`로 구현할 `인터페이스`가 있다면 더 좋음