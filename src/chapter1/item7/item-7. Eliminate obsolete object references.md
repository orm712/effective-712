다 쓴 객체 참조를 해제하라
## Java의 메모리 관리
- `Java`는 `가비지 컬렉터`를 갖춰, C/C++과 같이 직접 메모리를 관리해야 하는 언어보다 메모리 관리가 편리
- 하지만, 이것이 메모리 관리에 대해 전혀 신경쓰지 않아도 됨을 의미하진 않음
### 메모리 관리 예시 - 스택
```java
public class Stack {
	private Object[] elements;
	private int size = 0;
	private static final int DEFAULT_INITIAL_CAPACITY = 16;
	
	public Stack() {
		elements = new Object[DEFAULT_INITIAL_CAPACITY];
	}

	public void push(Object e) {
		ensureCapacity();
		elements[size++] = e;
	}
	
	public Object pop() {
		if (size == 0)
			throw new EmptyStackException();
		return elements[--size];
	}
	/**
	* 적어도 하나 이상의 원소를 위한 공간을 확보하기 위한 메서드
	* 배열의 확장이 필요할 때 마다 크기를 2배씩 늘린다.
	*/
	private void ensureCapacity() {
		if (elements.length == size)
		elements = Arrays.copyOf(elements, 2 * size + 1);
	}
}
```
- 위 코드는 배열을 활용한 `스택 구현체`임
- 코드상에 특별한 문제는 없지만, '`메모리 누수`'라는 중요 문제점이 존재
	- 위 `스택`을 사용하는 프로그램이 장기간 실행되면 GC 활동과 메모리 사용량이 늘어나 성능 저하가 발생
		- 심할땐 `디스크 페이징` 또는 `OutOfMemoryError`를 일으켜 프로그램이 비정상적으로 종료될 수 있음
			- `디스크 페이징`: `RAM`이 꽉 찼을때, 일부 데이터를 `디스크 공간(보조 메모리)`으로 `이동`하는 것
### 메모리 누수 지점 - `다 쓴 참조`
- 위 코드에서 누수가 발생하는 지점은 바로 원소를 꺼내는 `pop` 메서드
	- 스택의 크기가 줄어들 때, 꺼내진 객체는 GC에 의해 회수되지 않음
	- 여전히 스택이 객체들에 대한 `다 쓴 참조(obsolete reference)`를 갖고 있기 때문
		- `다 쓴 참조(obsolete reference)`: 앞으로 다시 사용하지 않을 참조
			- 위 코드에서는 `elements` 의 `활성 영역`(`size`보다 작은 인덱스를 갖는 원소) 외부의 참조들이 이에 해당
	- 이러한 메모리 누수는 `가비지 컬렉션 언어`에서 찾기 아주 까다로움
		- 객체 참조 하나가 살아있으면, 해당 객체가 `참조하는 또 다른 객체`(와 그 객체들이 참조하는 모든 객체)들을 `회수하지 못함`
		- 따라서 `몇 개의 객체 참조`로도 **`성능에 악영향`** 을 줄 수 있음
#### 해결 방법
- 다 쓴 참조에 대해 `null 처리(참조 해제)`를 하면 됨
- 이를 반영하면, 스택의 `pop` 메서드는 다음과 같이 수정할 수 있음
```java
public Object pop() {
	if (size == 0)
		throw new EmptyStackException();
	Object result = elements[--size];
	elements[size] = null; // 다 쓴 참조를 해제한다
	return result;
}
```
- 위 구현의 경우, `활성 영역 외부의 객체`에 실수로 접근할 경우 `NPE`과 함께 프로그램이 종료된다는 추가적인 이점이 존재
	- 만약 이렇게 null 처리를 하지 않았다면 내부에서 잘못된 일을 수행할 수 있음
### 올바른 null 처리
- 위 사례만 보면 `사용하고 난 모든 객체`를 `null 처리`해야할 것 같지만 *꼭 그렇진 않음*
- 객체를 `null 처리`하는 것은 **`예외적인 경우`** 여야 함
	- `다 쓴 참조`를 해제하는 가장 좋은 방법은 `참조가 담긴 변수`를 `유효 범위(scope) 밖으로 밀어내는 것`
- 그렇다면 `null 처리`가 필요한 때는?
	- **`비활성 영역`에 놓인 객체**가 더 이상 사용되지 않을 것임을 GC에게 알려야 할 때 사용
		- 예시의 스택 클래스와 같이 클래스에서 `메모리(element 배열)를 직접 관리`할 경우, `GC`는 메모리의 어느 영역이 `활성 영역인지 비활성 영역인지` **알 수 없음**
		- 따라서 `null 처리`를 함으로써 `비활성 영역의 객체`를 **더 이상 사용하지 않음**을 **알려야 함**
### 또 다른 메모리 누수 - 캐시
- `캐시` 역시 메모리 누수를 일으키는 주범
	- `객체 참조`를 캐시에 넣어둔 뒤, *해당 객체를 다 쓴 뒤에도* 이를 `방치`하는 경우가 종종 발생
- 해결법
	- [`WeakHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/WeakHashMap.html) 사용하기
		- 캐시 외부에서 `Key`를 참조하는 동안만 엔트리가 살아있는 캐시가 필요한 경우, `WeakHashMap`이 유용
		- [`WeakHashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/WeakHashMap.html): `약한 키(weak key)`를 사용하는 해시 테이블 기반 맵
			- 모든 키가 주어진 키에 대한 [`약한 참조(Weak Reference)`](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html)로 저장되는 맵으로, 키가 더 이상 일반적으로 사용되지 않는 엔트리가 자동으로 제거됨
	- `유효하지 않은 엔트리 삭제`하기
		- 캐시에서 보통 엔트리의 `유효 기간(lifetime)`을 정확히 정의하기 힘들어, `엔트리의 가치`를 *시간이 지남에 따라 떨어뜨리는 방식*을 주로 사용
		- 이때, 주기적으로 가치가 바닥난(*사용하지 않는*) 엔트리를 `청소`해야 함
			1. `백그라운드 스레드(ex. ScheduledThreadPoolExecutor)`를 활용하기 [#](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/concurrent/ScheduledThreadPoolExecutor.html)
				- `ScheduledThreadPoolExecutor`: `명령`이 `주어진 딜레이 이후`에, 또는 `주기적`으로 실행되도록 예약할 수 있는 `ThreadPoolExecutor`
					- `ThreadPoolExecutor`: 풀링된 여러 스레드 중 하나를 사용해 `주어진 작업(submitted task)`을 수행하는 `ExecutorService`
			2. `새 엔트리를 추가`할 때 `부수 작업`으로 수행하기
				- `LinkedHashMap`은 `removeEldestEntry` 메서드를 사용해 이 방식으로 처리
					- `removeEldestEntry`: Map에서 *가장 오래된 항목을 제거해야 할 경우* `true`를 반환하는 함수. 맵에 새 항목을 삽입한 뒤, `put`/`putAll`에 의해 호출됨. (디폴트로 `false`값을 반환)
					```java
					protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
						return false;
					}
					```
- 만약 더 복잡한 캐시를 만들고 싶다면 [`java.lang.ref`](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/package-summary.html) 패키지의 다양한 요소들을 직접 활용해야 함
	- 참고 - [Java Reference와 GC - Naver D2](https://d2.naver.com/helloworld/329631)
### 세 번째 메모리 누수의 주범 - 리스너/콜백
- 클라이언트가 콜백을 등록만 하고, 해지하지 않을 경우 콜백은 계속 쌓여감
	- 이때, 콜백을 `약한 참조(Weak Reference)`로 저장한다면 GC가 즉시 수거
## 정리
- `메모리 누수`는 겉으로 잘 드러나지 않아 긴 시간 잠복할 수 있음
	- 이러한 누수는 `힙 프로파일러`와 같은 디버깅 도구의 힘을 빌리거나, 코드 리뷰를 철저히 해야만 발견됨
- 이런 종류의 문제에 대한 예방법을 익혀두는 것이 매우 중요