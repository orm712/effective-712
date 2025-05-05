`wait`과 `notify`보다 `동시성 유틸리티`를 애용하라
## 중요도가 줄어든 wait과 notify
- `wait`과 `notify`는 `java.lang.Object`의 메서드로, `특정 객체 인스턴스`를 `모니터로 사용`해 `락을 획득하기 위해 대기(wait)`하고 `락을 기다리는 스레드 중 하나(또는 전체)를 깨우는(notify/notifyAll)` 동작을 수행
- Java 5 이후 `고수준의 동시성 유틸리티`(`java.util.concurrent`)들이 등장하여, 기존처림 `wait`과 `notify`로 하드코딩 하는 일이 줄어듦
- `wait`과 `notify`를 적절하게 사용하는 것은 까다롭기 때문에 이러한 `고수준 동시성 유틸리티`를 사용하는 것을 `권장`
### wait()
```java
synchronized (obj) {
	while (<조건이 충족되지 않음>)
		obj.wait(); // 락을 놔주고, 깨어날 경우 다시 잡음
	... // 조건이 충족되었을 때의 동작을 수행
}
```
- `스레드`가 어떤 `조건이 충족되길 기다리게` 할 때 사용
	- 예를 들어 `락 객체`의 `wait()`은 반드시 `그 객체를 잠근 동기화 영역 내`에서 호출해야 함
- `wait` 메서드를 사용할 때는 반드시 `대기 반복문(wait loop)` 관용구를 사용해야 하며, *반복문 밖에서는* 절대로 **호출하지 말아야 함**
	- `반복문`이 `wait` *호출 전/후*의 `상태를 검사`하는 역할이기 때문
- 위처럼 `wait()` 전 반복문에서 `조건을 검사`해, 조건을 충족한 경우 `wait`을 건너뛰는 것은 `응답 불가 상태`에 빠지는 것을 **예방**하기 위함
	- *만약 조건이 이미 충족되었는데* 스레드가 `notify`(또는 `notifyAll`)를 먼저 호출한 뒤 `wait`할 경우 해당 `스레드를 다시 깨울 수 있을지` **보장할 수 없음**
- *대기 후* `조건을 검사`해 *조건이 충족되지 않은 경우* `다시 대기`하도록 하는 것은 `안전 실패`를 **막는 조치**
	- 만약 *조건이 충족되지 않았는데 스레드가 작업을 진행*하는 경우, `락으로 인해 보호되는 불변식`이 **깨질 수 있음**
	- 다음과 같이 *조건이 충족되지 않아도* `스레드가 깨어날 수 있는 몇 가지 이유`가 있음
		1. 스레드가 `notify`를 호출하고, `대기 중이던 스레드`가 깨어난 시간 사이에 다른 스레드가 `락을 획득`하고 `보호중이던 상태`를 변경 함
		2. *조건이 만족되지 않았음에도*, 다른 스레드가 `실수` 또는 `악의적으로 nofity를 호출`함. 
			- `공개적으로 접근 가능한 객체`를 `락으로 사용해 대기하는 클래스`는 이러한 위험에 노출될 수 있음.
			- *`외부에 노출된 객체의 동기화된 메서드` 안에서 호출되는* `wait`은 이러한 문제가 발생하기 쉬움
		3. `깨우는 스레드`가 *다른 스레드를 깨우는데* **지나치게 "관대"해서**, `대기 중인 스레드` 중 `일부만 조건이 충족`되어도 `notifyAll`을 호출해 모든 스레드를 깨울 수 있음
		4. (드물게) 대기중인 스레드가 `notify` 없이도 깨어나는 `허위 각성(spurious wakeup)`이라는 현상이 발생
			- 보통 스레드 간 `경쟁 상태`가 존재해, 한 스레드가 *다른 스레드가 깨어나 실행되기 전* 조건을 바꿔버리는 경우 발생
### notify() / notifyAll()
```java
class demo {
    // variable to check if part1 has returned
    // volatile used to prevent threads from
    // storing local copies of variable
    volatile boolean isSent = false;
 
    // method synchronized on this
    // i.e. current object of demo
    synchronized void send()
    {
        System.out.println("Welcome to India");
        isSent = true;
        System.out.println(
            "Thread t1 about to surrender lock");
        notify();
    }
 
    // method synchronized on this
    // i.e. current object of demo
    synchronized void receive()
    {
        // loop to prevent spurious wake-up
        while (!isSent) {
            try {
                System.out.println("Thread t2 waiting");
                wait();
            }
            catch (Exception e) {
                System.out.println(e.getClass());
            }
        }
        System.out.println("메세지 도착함");
    }
}
 
public class Main {
 
    public static void main(String[] args)
    {
        demo obj = new demo();
        Thread t1 = new Thread(new Runnable() {
            public void run() { obj.send(); }
        });
 
        // Thread t2 will call part2()
        Thread t2 = new Thread(new Runnable() {
            public void run() { obj.receive(); }
        });
 
        t2.start();
        t1.start();
    }
}
```
- `notify()`
	- 특정 객체의 모니터에 대해 `대기 중인 스레드 중 하나`를 임의로 **깨우도록 알리는 메서드**
	- `임의의 단일 스레드`를 깨우기 때문에, 스레드들이 유사한 작업을 수행하는 경우 `상호 배타적 락`을 구현하는데 사용할 수 있음
- `notifyAll()`
	- 특정 객체의 모니터에 대해 `대기 중인 모든 스레드`를 **깨우도록 알리는 메서드**
- 일반적으로 `notifyAll을 사용`하는게 `합리적이고 안전`함
	- `깨어나야 할 스레드` 모두를 깨우므로 `항상 정확한 결과`를 얻을 것이기 때문
	- *다른 스레드까지 깨어날 수 있으나*, 그 스레드들 중 `조건이 충족되지 않은 스레드`는 `다시 대기할 것`이므로 **프로그램 정확성에는 영향을 주지 않음**
- 만약 *모든 스레드가 `같은 조건을 기다리고`*, 조건이 충족되었을 때 `한 스레드만 혜택`을 받을 수 있는 경우 `notifyAll` 대신 `notify`를 사용해 `최적화` 할 수 있음
- 하지만, 앞서 *외부로 공개된 객체에 대한 notify를 방어하고자 반복문 안에서 wait을 호출한 것 처럼*, `notify` 대신 `notifyAll`을 쓰면 관련 없는 스레드가 `실수 또는 악의적`으로 `wait`을 호출하는 공격을 보호할 수 있음
	- 만약 그러한 스레드가 중요한 `notify`를 삼켜버릴 경우, `깨어났어야 할 다른 스레드들`이 `영원히 대기`할 수 있기 때문
## 고수준 동시성 유틸리티의 종류
- `java.util.concurrent`의 고수준 유틸리티로는 `동시성 컬렉션(concurrent collection)`, `실행자 프레임워크`, `동기화 장치(synchronizer)` 세 범주가 있음
### 동시성 컬렉션
- `List`, `Queue`, `Map`과 같은 표준 컬렉션 인터페이스의 `고성능 동시성 구현체`
	- 기존의 `동기화한 컬렉션(synchronized collection)`을 `동시성 컬렉션`으로 대체할 경우 애플리케이션의 성능이 훨씬 좋아짐
		- 예를 들어 `Collections.synchronizedMap`보다 `ConcurrentHashMap`을 쓰는 것이 훨씬 좋음
		- `동기화한 컬렉션`: 내부의 모든 작업들이 `synchronized` 키워드를 달고 있는 형태의 컬렉션
- `높은 동시성`을 위해, 구현체들은 스스로의 `동기화`를 각각의 `내부에서 관리`
	```java
	// 예시
	// ConcurrentHashMap의 해시테이블 역할을 하는
	// Segment의 clear()
	final void clear() {
		// 작업 전 먼저 락을 획득
		lock();
		try {
			HashEntry<K,V>[] tab = table;
			for (int i = 0; i < tab.length ; i++)
				setEntryAt(tab, i, null);
			++modCount;
			count = 0;
		} finally {
			// 메서드 완료 전 락 반환
			unlock();
		}
	}
	```
	- 따라서 이들로부터 `동시성을 제외`시키는 것은 `불가능`하며, *외부에서 `락을 추가로 사용`할 경우* `속도가 느려짐`
- 이로 인해, `메서드들을 원자적으로 묶어 호출`하는 것 역시 `불가능`함
- 이를 보완하고자, *여러 기본 동작들을* **`단일 원자 연산`로 결합**하는 `'상태 의존적 수정(state-dependent modify)'` 연산들이 추가됨
	- 이러한 연산들은 `동시성 컬렉션`에서 유용성이 입증되어 Java 8에서 `컬렉션 인터페이스`의 `디폴트 메서드`로 추가됨
#### 예시 - Map.putIfAbsent(key, value)
- `Map`의 `putIfAbsent(key, value)`는 주어진 키에 대해 *매핑된 값이 있는지 확인*하고, `없을 경우 값을 넣는` 연산
	- 만약 기존 값이 있었다면 `그 값을 반환`하고, 없었다면 `null을 반환`
```java
Map<String, Integer> map = ...;

// 기본 연산의 조합
Integer i = map.getOrDefault(k, null);
if(i == null) {
	map.put(k, v);
}
return i;

// putIfAbsent 사용
Integer i = map.putIfAbsent(k, v);
```
- 이러한 연산은 기본 연산인 `getOrDefault`와 `put`을 조합한 연산으로 볼 수 있음
	- 즉, `멀티스레드 환경`이라면 두 연산 사이에 `경쟁 상태`가 발생할 수 있음
	- 따라서, 둘을 하나로 묶은 `putIfAbsent` 메서드가 나오게 된 것
- 이를 통해 `스레드 안전한 정규화 맵(canonicalizing map)`을 쉽게 구현할 수 있음
	- `정규화 맵(canonicalizing map)`: 동일한 값에 대해 여러 번 만들 지 않고, 이미 존재하는 객체를 재사용 하는 정규화 캐시
#### 예시 - String.intern 흉내내기
```java
// 예시
public static void whenIntern_thenCorrect() {  
    String s1 = "abc";  
    String s2 = new String("abc");  
    String s3 = new String("foo");  
    String s4 = s1.intern();  
    String s5 = s2.intern();  
  
    System.out.println(s3 == s4); // false
	// s1이 "abc"를 문자열 상수 풀에 저장했으므로
	// s2.intern()은 문자열 상수 풀에 저장된 "abc"인 s1을 가리킴
	System.out.println(s1 == s2); // false
    System.out.println(s1 == s5); // true
}
```
- String의 `intern()` 메서드는 `문자열 객체`에 대해 `정규화된 표현`을 반환하는 메서드
- 정확히는 `힙 메모리`에 `문자열 객체의 복사본`을 `생성`한 뒤, `문자열 상수 풀에 저장`하는 메서드
	- 만약 `문자열 상수 풀`에 `동일한 내용의 다른 문자열`이 있는 경우, *새로운 객체를 생성하는 대신* 해당 문자열을 가리키게 함
- 이러한 `String.intern()`을 동시성 컬렉션인 `ConcurrentMap`으로 흉내내면 다음과 같음
```java
private static final ConcurrentMap<String, String> map =
	new ConcurrentHashMap<>();

// 일반 구현
public static String intern(String s) {
	String previousValue = map.putIfAbsent(s, s);
	// Map에 s가 이미 존재한다면 해당 문자열을(previousValue),
	// 그렇지 않다면 상수 풀에 저장한 값(s)을 반환
	return previousValue == null ? s : previousValue;
}

// 최적 구현
// ConcurrentHashMap은 get과 같은 검색(retrieval) 연산에 특화됨
// 따라서 get을 먼저 호출한 뒤 필요할 경우 putIfAbsent를 호출하도록 하여 최적화
public static String intern(String s) {
	String result = map.get(s);
	
	if (result == null) {
		result = map.putIfAbsent(s, s);
		if (result == null)
			result = s;
	} 
	return result;
}
```
#### 작업 완료까지 차단되는 형태의 컬렉션
- 일부 `컬렉션 인터페이스`는 *작업이 성공적으로 완료될 때까지* `차단`되도록 확장됨
```java
// BlockingQueue의 구현체 중 하나인 LinkedBlockingQueue의 take()
public E take() throws InterruptedException {
	E x;
	int c = -1;
	final AtomicInteger count = this.count;
	final ReentrantLock takeLock = this.takeLock;
	takeLock.lockInterruptibly();
	try {
		// Queue가 비어있는 동안,
		// 즉, count가 0인 동안 대기함 
		while (count.get() == 0) {
			notEmpty.await();
		}
		x = dequeue();
		c = count.getAndDecrement();
		if (c > 1)
			notEmpty.signal();
	} finally {
		takeLock.unlock();
	}
	if (c == capacity)
		signalNotFull();
	return x;
}
```
- 예를 들어 `Queue`를 확장한 `BlockingQueue` 인터페이스의 경우, (*큐가 비어있으면 대기하다가*) 큐의 첫 원소를 꺼내는 `take`와 같은 메서드를 갖고 있음
- 이러한 특성 때문에 `BlockingQueue`는 `작업 큐(work queue, 생산자-소비자 큐)`로 쓰기 적합
	- `작업 큐`: 하나 이상의 `생산자(producer) 스레드`가 **작업을 큐에 추가**하고, 하나 이상의 `소비자(consumer) 스레드`가 **큐에 있는 작업을 꺼내 처리**하는 형태
- 또한, 대부분의 `실행자 서비스(ex. ThreadPoolExecutor) 구현체`에서 이 `BlockingQueue`를 사용하고 있음
### 동기화 장치(Synchronizers)
- 스레드가 다른 스레드를 기다릴 수 있게 하여, 서로의 작업을 조율할 수 있게 해주는 `객체`
- 가장 일반적으로 사용되는 `동기화 장치`는 `CountDownLatch`와 `Semaphore`
	- `CyclicBarrier`와 `Exchanger`는 덜 사용됨
- 가장 `강력한 동기화 장치`는 `Phaser`
#### CountDownLatch
- `하나 이상의 스레드`가 *다른 하나 이상의 스레드가 작업을 끝낼 때까지* 기다리도록 하는 `일회성 장벽(barrier)`
- `CountDownLatch`의 생성자는 `int` 값을 받는데, 이는 `모든 대기 중인 스레드`가 *진행되도록 허용하기 위해서* `래치`에서 `countDown` 메서드가 `몇 번 호출되어야 하는지`를 뜻함
	- 달리 말하면, 특정 `CountDownLatch(N)`의 `await()`를 호출중인 스레드는 해당 `래치`의 `countDown()`이 N번 호출될 때까지 대기한다는 뜻
##### 예시
```java
// 동시 실행 시간을 재는 예시 프레임워크
public static long time(Executor executor, int concurrency, Runnable action) throws InterruptedException {
	CountDownLatch ready = new CountDownLatch(concurrency);
	CountDownLatch start = new CountDownLatch(1);
	CountDownLatch done = new CountDownLatch(concurrency);
	
	for (int i = 0; i < concurrency; i++) {
		executor.execute(() -> {
			// 타이머에게 준비가 되었음을 알림
			ready.countDown();
			try {
				start.await(); // 모든 Worker 스레드가 준비될 때 까지 기다림
				action.run();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				done.countDown(); // 타이머에게 작업을 마쳤음을 알림
			} 
		});
	}
	ready.await(); // 모든 Worker가 준비될 때까지 기다림
	long startNanos = System.nanoTime();
	// Worker들을 깨움 (즉, start.await() 중이던 스레드들이 다음 작업을 수행할 수 있도록 함)
	start.countDown(); 
	// 모든 Worker들이 일을 끝마칠때까지 대기 (즉, 모든 스레드들이 작업을 끝내고 done.countDown()을 호출하기를 기다림)
	done.await(); 
	return System.nanoTime() - startNanos;
}
```
- 위 코드는 세 개의 `CountDownLatch`를 사용하는 타이머 코드임
	- `ready` 래치는 Worker 스레드들이 `준비가 됐음`을 `타이머 스레드`(*time 메서드를 호출하고 있는 스레드*)에게 **통지**하기 위해 사용
	- `start` 래치는 *시간 측정 준비를 끝낸* `타이머 스레드`가 `Worker` **스레드들이 작업을 시작하도록** 깨우기 위해 사용
	- `done` 래치는 `Worker` 스레드 중 *마지막으로 동작을 마친 스레드*가 `done.countDown()`을 호출하여 **작업이 끝났음**을 `타이머 스레드`에게 **통지**하기 위해 사용
		 - 깨어난 `타이머 스레드`는 `작업에 걸린 시간(System.nanoTime() - startNanos)`을 반환
 - 위 코드의 세부사항들은 다음과 같음
	 1. `time` 메서드에 넘겨진 `실행자(Executor executor)`는 `concurrency(int concurrency)` 매개변수로 지정된 `동시성 수준`만큼 스레드를 만들 수 있어야 함
		 - 그렇지 못할 경우, `time` 메서드가 절대 끝나지 않는 `스레드 기아 교착상태(thread starvation deadlock)`가 발생
	 2. `InterruptedException`을 잡은 `Worker 스레드`는 `Thread.currentThread().interrupt()` 관용구를 사용해 **인터럽트를 되살리고**, 자신은 `run()` 메서드에서 빠져나옴
		 - 이렇게 해야 실행자가 인터럽트를 적절히 처리할 수 있음
			 - *만약 위처럼 하지 않을 경우*, `InterruptedException`을 잡은 순간 `스레드의 인터럽트 상태`가 **`자동으로 초기화`** 되기 때문
				 - 즉, `Thread.interrupted()`의 상태가 `false`로 바뀌게 됨
		 - `InterruptedException`란, 스레드가 `wait`,`sleep`, 또는 다른 방식으로 `점유(occupied)` 중일때(*정확히는 이러한 활동들의 `활동 전`이거나  `활동 중`인*) 스레드가 `인터럽트될 때 발생`하는 예외
			 - 즉, 다른 코드에서 해당 스레드에 대해 `interrupt()` 메서드를 호출했을때 발생
	 3. `시간 간격`을 잴 땐, 항상 `System.nanoTime`을 사용할 것
		 - `System.currentTimeMillis`보다 `비교적 더 정확`하고 `정밀(나노초 단위)`하며, 시스템의 실시간 시계의 시간 보정에 영향을 받지 않음
			 - `System.currentTimeMillis`의 경우 "1970년 1월 1일 00:00:00 UTC"으로 부터 지금까지 몇 밀리초가 지났는지 반환하지만, `System.nanoTime`은 *임의의 한 시간으로부터* `몇 나노초가 지났는지를 반환`하기 때문
		 - `System.currentTimeMillis`는 시스템 시계 기반, `System.nanoTime`은 시스템 타이머 기반
	 4. 정밀한 시간 측정을 하고싶다면, [jmh](https://github.com/openjdk/jmh)와 같은 특수한 프레임워크를 사용할 것
		 - 이처럼 프로그램을 구성하는 "작은 작업"에 대해 벤치마킹 하는것을 `Microbenchmark`라고 함
- 추가로 위 코드에서 사용한 3개의 `카운트다운 래치`는 하나의 `CyclicBarrier`(또는 `Phaser`)로 대체할 수 있음
# 핵심 정리
- `wait`과 `notify`를 직접 쓰는 것은 '동시성 어셈블리 언어'로 프로그래밍하는 것과 같음
	- 반면 `java.util.concurrent`은 고수준 언어에 비유할 수 있음
- 코드를 새로 작성하는 경우, `wait`과 `notify`를 사용할 이유는 **거의 없음**
	- 이들을 사용해 `레거시 코드를 유지보수`하는 경우, 다음과 같이 사용할 것
		- `wait`은 항상 `표준 관용구`를 사용해 `while 문 안에서 호출`할 것
		- 일반적으로 `notifyAll` 메서드를 `notify`보다 **우선적으로 쓸 것**
			- 만약 `notify`를 쓰는 경우 `응답 불가 상태`에 빠지지 않도록 주의하라
# 참고
- [wait and notify() Methods in Java | Baeldung](https://www.baeldung.com/java-wait-notify)