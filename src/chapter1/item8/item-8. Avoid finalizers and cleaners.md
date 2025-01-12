finalizer와 cleaner 사용을 피하라
## Java의 객체 소멸자
- Java에서는 2가지 객체 소멸자, `finalizer`와 `cleaner`를 제공함
	- 그 중 `finalizer`의 경우 *나름의 쓰임*이 있긴 하지만 **`사용하지 않는것`** 을 `권장`
		- *`Java`에서 제공하는 라이브러리에서도 사용*되고 있지만, **예측할 수 없고**, 상황에 따라 **위험**할 수 있으며 **오동작**, **이식성 문제** 등 다양한 **`위험 부담`** 이 있기 때문
		- 또한, Java 9 버전 부터 `deprecated API`로 지정하며, `cleaner`를 **대안**으로 소개
	- `cleaner`의 경우 `finalizer`보다 덜 위험하지만, 여전히 `예측 불가능`/`느림`/`(일반적으로)불필요` 하다는 **단점**이 존재
### 문제 - 굼뜨고 예측 불가능한 호출 시점
- `finalizer`와 `cleaner` 모두 호출 즉시 수행된다는 보장이 없음
	- 객체에 접근할 수 없게 된 후 이들이 실행되기까지 얼마나 걸릴지 알 수 없으므로, 이들로 시간에 민감한 작업은 수행할 수 없음
	- ex) 시스템이 *동시에 열 수 있는 파일 갯수는 제한*이 있는데, `finalizer`나 `cleaner`에게 파일 닫기를 맡길 경우 실행이 게으르게 수행되어 파일을 계속 열어 둘 수도 있고 이로 인해 새로운 파일을 열지 못해 프로그램이 비정상 종료될 수 있음
- `finalizer`/`cleaner`의 수행은 전적으로 `GC 알고리즘`에 달렸으며, `GC 구현마다 천차만별`임
	- 따라서 이에 의존하는 동작들이 `테스트한 JVM`에서는 `괜찮을 수` 있지만, *실제 현장에서는* `제대로 동작하지 않을 수` 있음
- 클래스에 `finalizer`를 사용하면 해당 `인스턴스의 자원 회수`가 `임의로 지연`될 수 있음
	- 때문에 현업에서도 특정 객체 수 천개가 `finalizer 대기열`에서 회수되기만을 기다리는 사례도 발생
	- 이는 `finalizer` 스레드가 다른 `애플리케이션 스레드`보다 낮은 우선순위를 가져 제대로 실행되지 않았기 때문
		- `Java 언어 명세`는 *어떤 스레드가* `finalizer`를 수행할지 `명시하지 않으며`, `cleaner`의 경우 클래스 작성자가 이를 수행할 **`스레드를 제어`** 할 수 있긴 하지만 여전히 `GC의 통제하`에 있다는 점은 동일
### 또 다른 문제 - 보장되지 않는 수행 여부
- 자바 언어 명세는 `finalizer`/`cleaner`의 수행 여부도 보장하지 않음
	- 즉, `접근 불가능한 객체`에 딸린 `종료 작업`을 *수행하지 못한채*로 **프로그램이 종료**될 수 있음
	- 따라서 `영구 상태(persistent state)`를 갱신하는 작업에서는 `finalizer`/`cleaner`에 의존해서는 안 됨
		- 가령, `데이터베이스와 같은 공유 자원`의 `영구 락(persistent lock)`을 해제하는 것을 `finalizer`/`cleaner`에 맡길 경우 분산 시스템 전체가 `서서히 마비`될 수 있음
- `System.gc`, `System.runFinalization` 메서드를 호출하는 것은 `finalizer`/`cleaner`의 실행 가능성을 높힐 순 있으나 역시 실행 여부를 보장해주진 않음
	- `System.runFinalizersOnExit`, `Runtime.runFinalizersOnExit`와 같은 메서드들이 이들을 보장해주겠다고 했으나, `심각한 결함`때문에 수십 년간 `deprecated` 됨
		- [System 공식 API](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html)와 [Java Thread Primitive Deprecation](https://docs.oracle.com/javase/9/docs/api/java/lang/doc-files/threadPrimitiveDeprecation.html)에 따르면 다음을 이유로 들고있음
		```text
		멀티스레드 환경에서 다른 스레드가 해당 객체를 조작하는 동안 finalizer가 해당 객체에 호출되어
		불규칙한 동작이나 교착 상태가 발생할 수 있기 때문에 Runtime.runFinalizersOnExit은 더이상 사용되지 않습니다.
		객체가 finalize 당하는 클래스가 이 호출을 방어(defend)하도록 코딩 되어있다면, 이러한 문제를 방지할 수 있지만 대부분의 프로그래머는 이를 방어하지 않습니다.
		이들은 finalizer가 호출되는 시점에 객체가 죽었다고 가정하기 때문입니다.
		또한, 이 호출은 VM-global flag를 설정한다는 점에서 "thread-safe" 하지 않습니다.
		```
## finalizer의 또 다른 부작용
### 예외 무시
- `finalizer`는 동작 중 발생한 예외를 무시하며, 처리할 작업이 남았더라도 그 순간 종료됨 (**`경고조차 출력하지 않는다!`**)
	- 따라서 잡지 못한 예외로 인해 객체는 마무리가 덜 된 채로 남을 수 있으며, 이를 다른 스레드가 사용할 경우 동작을 예측할 수 없음
- 다만, `cleaner`는 자신의 스레드를 통제하므로 이러한 문제는 없음
### 보안 문제
- `finalizer`를 사용한 클래스는 `finalizer 공격`에 노출됨
- `finalizer 공격`?
	- `생성자` 또는 `직렬화 과정(readObject/readResolve)`에서 예외가 발생해 `"일부분만 생성"된 객체`에서 악의적인 `하위 클래스`의 `finalizer`가 실행할 수 있게 됨
	- 이 `finalizer`는 `정적 필드`에 **자신의 참조**를 `할당`시켜, *GC가 회수해가지 못하게* 막을 수 있음. 즉, `"일부분만 생성"된 객체`가 회수되지 않음
	- 이렇게 만들어진 `일부분만 생성된 객체`의 메서드를 호출할 수 있게 되면, 애초에 허용되지 않은 작업을 수행하는 등 `취약점`이 발생
- 이들을 막기 위해서는 클래스를 `final`로 선언해 *상속을 막거나*, *아무 일도 하지 않는* `finalize` 메서드를 만든 뒤 `final`로 선언해야 함
```java
class CLass {
	...
	public final void finalize() {}
}
```
## finalizer/cleaner의 성능 문제
- `finalizer`/`cleaner`를 사용할 경우, 이들이 *GC의 효율을 떨어뜨리기 때문*에 **훨씬 느려짐**
	- 저자의 테스트에 따르면 `try-with-resources`문을 사용해 `GC가 직접 수거`하는데는 `12ns`가 걸렸지만, `finalizer`/`cleaner`를 사용했을때 `550ns`/`500ns` 가량으로 약 50배 이상 느림
### finalizer/cleaner의 대안 - AutoCloseable
- 파일, 스레드와 같이 `종료해야 할 자원`을 안에 담고있는 객체 클래스가 `AutoCloseable`을 구현하고, 클라이언트에서 해당 인스턴스를 모두 사용한 뒤 `close` 메서드를 호출하도록 하면 됨
	- 일반적으로는 예외 발생시에도 정상 종료되도록 `try-with-resources`를 사용해야 함
	- 추가로, 각 인스턴스는 자신이 닫혔는지를 기록 및 추적하는 것이 좋음
		- 가령 `close` 메소드가 호출되면 이 객체가 더 이상 유효하지 않음을 필드에 기록하고, 다른 메서드는 해당 필드를 검사하여 객체가 유효하지 않은 상태면 `IllegalStateException`을 던지도록 하는 것
## finalizer/cleaner의 용도
### close 메서드의 안전망
- 간혹 자원의 소유자가 `close` 메서드를 호출하지 않을 경우를 대비해 `finalizer`/`cleaner`를 설정하면 이들이 `안전망 역할`을 해줌
	- 이들이 즉시(또는 끝까지) 호출되리라는 보장은 없지만, 늦게라도 자원 회수를 해줄 것
- `자바 라이브러리`에서도 이러한 안전망 역할의 `finalizer`를 채용한 클래스들이 있음
	- `FileInputStream`, `FileOutputStream`, `ThreadPoolExecutor`가 대표적
		- 다만, *최근 버전의 JDK*에서는 이들이 `finalize`를 *구현하고 있지 않음*
```java
// FileOutputStream의 finalize() 구현
    /**
     * Ensures that the <code>close</code> method of this file input stream is
     * called when there are no more references to it.
     *
     * @deprecated The {@code finalize} method has been deprecated.
     *     Subclasses that override {@code finalize} in order to perform cleanup
     *     should be modified to use alternative cleanup mechanisms and
     *     to remove the overriding {@code finalize} method.
     *     When overriding the {@code finalize} method, its implementation must explicitly
     *     ensure that {@code super.finalize()} is invoked as described in {@link Object#finalize}.
     *     See the specification for {@link Object#finalize()} for further
     *     information about migration options.
     *
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileInputStream#close()
     */
    @Deprecated(since="9")
    protected void finalize() throws IOException {
        if ((fd != null) &&  (fd != FileDescriptor.in)) {
            /* if fd is shared, the references in FileDescriptor
             * will ensure that finalizer is only called when
             * safe to do so. All references using the fd have
             * become unreachable. We can call close()
             */
            close();
        }
    }
```
- 출처: [jdk/src/java.base/share/classes/java/io/FileInputStream.java at 3789983e89c9de252ef546a1b98a732a7d066650 · openjdk/jdk](https://github.com/openjdk/jdk/blob/3789983e89c9de252ef546a1b98a732a7d066650/src/java.base/share/classes/java/io/FileInputStream.java)
### 자바 피어 ( `네이티브 피어(native peer)`와 연결된 객체 )
- `네이티브 피어`?
	- 일반 자바 객체가 `네이티브 메서드`를 통해 기능을 위임한 `네이티브 객체`
- `네이티브 피어`는 Java 객체가 아니므로, GC는 존재를 알지 못함
	- 따라서 `자바 피어`를 회수할 때 `네이티브 피어`까지 회수하진 못함
- 이를 `finalizer`/`cleaner`를 통해 처리할 수 있음
	- 다만, `성능 저하`와 `네이티브 피어`가 `중요한 자원`을 갖고있지 않을때만 사용해야 함
	- 그렇지 못한 경우에는 `close` 메서드를 사용해야 함
## `cleaner` 사용 예제
- `cleaner`는 사용하기 까다로운 측면이 존재
	- `cleaner`는 `finalizer`와 달리 `public API`에 나타나지 않음
```java
// cleaner를 안전망으로 사용하는 autocloseable class
public class Room implements AutoCloseable {
	private static final Cleaner cleaner = Cleaner.create();
	
	// 청소가 필요한 자원. 절대로 Room을 참조해서는 안 된다!
	private static class State implements Runnable {
		int numJunkPiles; // 방(room) 안의 쓰레기 수
		State(int numJunkPiles) {
			this.numJunkPiles = numJunkPiles;
		}
		// close 메서드 or cleaner에 의해 호출된다.
		@Override public void run() {
			System.out.println("Cleaning room");
			numJunkPiles = 0;	
		}
	}
	// 방의 상태를 나타내는 변수. cleanable과 공유한다.
	private final State state;
	
	// cleanable 객체. gc의 대상이 되면 방을 청소한다.
	private final Cleaner.Cleanable cleanable;

	// Room의 생성자
	public Room(int numJunkPiles) {
		state = new State(numJunkPiles);
		// cleaner에 room(this)과 state를 등록해 cleanable 객체를 얻는다.
		cleanable = cleaner.register(this, state);
	}
	@Override public void close() {
		cleanable.clean();
	}
}
```
- 방을 뜻하는 Room 클래스를 수거하기 전 `청소(clean)`를 수행해야 하는 케이스라고 가정
	- Room은 `Autocloseable`을 구현
- *방을 청소할 때 수거할 자원*을 담고있는 `State` 클래스는 `static 중첩 클래스`로 선언되어 있음
	- 만약 `static이 아닌 중첩 클래스`로 작성했다면, 바깥 객체인 Room을 자동으로 참조해 `순환 참조`가 발생하고, GC에 의해 인스턴스가 회수될 수 없게 됨
		- 이와 비슷하게, `람다`도 `바깥 객체의 참조`를 갖기 쉬우니 *사용하지 않는게 좋음*
	- 위 예제에서는 단순히 정수 값 하나를 갖지만, 좀 더 현실적일려면 *네이티브 피어를 가리키는 포인터 값*을 담고있는 `final long` 필드 인 것이 나음
	- `State`의 `run`은 `cleanable`에 의해 딱 한 번만 호출됨
		 1. Room의  `close` 메서드를 호출하는 경우
		 2. (*GC가 회수해갈때 까지 close를 호출하지 않으면*)`cleaner`가 직접 `run`을 호출
 - 위 케이스에서 `cleaner`는 안전망으로만 사용됨
	 - 따라서 클라이언트 측에서 모든 Room 생성을 `try-with-resources`블록으로 감싼 경우, 자동 청소는 필요하지 않음
	```java
	public class Adult {
		public static void main(String[] args) {
			try (Room myRoom = new Room(7)) {
				// 기대한대로 먼저 "Goodbye" 한 뒤, Room의 close와 함께 "Cleaning room"이 출력됨
				System.out.println("Goodbye");
			}
		}
	}
	```
	- 반면, 그냥 사용할  경우 `clean()`은 언제 수행될 지 알 수 없음
## C++의 소멸자(destructor)와의 차이
- Java의 `finalizer`/`cleaner`는 C++의 `destructor`와 다른 개념
	- `destructor`의 경우, *특정 객체와* `관련된 자원을 회수`하는 **`보편적인 방법`**(이자 생성자의 대척점)임
- 따라서 `비메모리 자원`을 회수할 때, `destructor`를 사용
	- 반면, Java에서는 `try-with-resources` 및 `try-finally`문을 사용
- Java에서는 `접근 불가능한 객체의 회수`를 `GC`가 담당하고, 프로그래머에게는 *아무것도 요구되지 않음*

## 정리
- `cleaner`(Java 8까진 `finalizer`)는 `안전망` 또는 *중요하지 않은* `네이티브 자원 회수`용으로만 사용할 것
- 또한 위 경우에도 불확실성, 그리고 성능 저하를 주의해야 함