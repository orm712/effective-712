스레드보다는 실행자, 태스크, 스트림을 애용하라
## Java 5 이전의 작업큐
```java
public abstract class WorkQueue { 
    private final List queue = new LinkedList(); 
    private boolean stopped = false; 
 
    protected WorkQueue() { new WorkerThread().start(); } 

	// 클라이언트가 요청한 작업 등록
    public final void enqueue(Object workItem) { 
        synchronized (queue) { 
            queue.add(workItem); 
            queue.notify(); 
        } 
    } 
	
	// 큐에 작업 중단 요청
    public final void stop()  { 
        synchronized (queue) { 
            stopped = true; 
            queue.notify(); 
        } 
    } 
    protected abstract void processItem(Object workItem) throws InterruptedException; 

	// 작업을 수행하는 백그라운드 스레드
	private class WorkerThread extends Thread { 
	    public void run() { 
	        while (true) {  // Main loop 
	            Object workItem = null; 
	            synchronized (queue) { 
	                try { 
	                    while (queue.isEmpty() && !stopped) 
	                        queue.wait(); 
	                } catch (InterruptedException e) { 
	                    return; 
	                } 
	                if (stopped) 
	                    return; 
	                workItem = queue.remove(0); 
	            } 
	            try { 
	                processItem(workItem); // No lock held 
	            } catch (InterruptedException e) { 
	                return; 
	            } 
	        } 
	    } 
	}
} 

 
class DeadlockQueue extends WorkQueue { 
    protected void processItem(final Object workItem) 
            throws InterruptedException { 
        // Create a new thread that returns workItem to queue 
        Thread child = new Thread() { 
            public void run() { enqueue(workItem); } 
        }; 
        child.start(); 
        child.join(); // 부모 스레드(queue)에서 자식 스레드(child) 기다림
    } 
} 
```
- Effective Java의 초판(2001년 6월 출판)에서 다음과 같은 `작업 큐(Work Queue)`를 소개함
## Java 5의 `java.util.concurrent`
- 2004년 9월, `java.util.concurrent` 패키지를 포함하는 `Java 5`가 등장
	- 해당 패키지는 `실행자 프레임워크(Executor Framework)`라고 하는 `'인터페이스 기반의 유연한 태스크 실행 기능'`을 포함하고 있음
- 예시로, 위에서 소개된 작업 큐보다 뛰어난 `작업 큐`를 다음과 같은 코드로 생성 가능
```java
// 작업 큐(실행자 서비스) 생성
ExecutorService exec = Executors.newSingleThreadExecutor();

// 실행자에게 실행할 태스크 넘기기
exec.execute(runnable);

// 실행자 종료시키기 (해당 작업에 실패해도, VM은 종료되지 않을 것임)
exec.shutdown();
```
## 실행자 서비스의 주요 기능
- `특정 태스크가 완료`되기를 `기다리기`
	- `exec.submit(...).get() // Future 반환`
- 태스크 모음 중 '아무거나 하나(`invokeAny`)' 또는 '모든 태스크(`invokeAll`)'가 완료되기를 `기다리기`
	- `invokeAny(Collection<? extends Callable<T>> tasks)`
	- `invokeAll(Collection<? extends Callable<T>> tasks)`
- `실행자 서비스`가 `종료`하기를 기다리기(`awaitTermination`)
	- `awaitTermination(long timeout, TimeUnit unit)`
- 완료된 `태스크들의 결과`를 `차례대로 받기`(`ExecutorCompletionService` 사용)
	- 제공된 실행자를 사용해 `태스크`을 실행하는 `CompletionService`로, *submit된 태스크가 완료되면* `take`를 사용해 접근할 수 있는 `Queue`에 배치함
	```java
	// 예시 코드
	 void solve(Executor e,
            Collection<Callable<Result>> solvers)
     throws InterruptedException, ExecutionException {
	     CompletionService<Result> ecs
	         = new ExecutorCompletionService<Result>(e);
		// 매개변수로 주어진 solver 집합을 동시에 실행
	     for (Callable<Result> s : solvers)
	         ecs.submit(s);
	     int n = solvers.size();
	     for (int i = 0; i < n; ++i) {
			 // Queue에 접근해 반환된 각각의 결과에 대해
			 // Result를 인자로 받는 메서드 `use(Result r)`를 실행
	         Result r = ecs.take().get();
	         if (r != null)
	             use(r);
	     }
	 }
	```
- 태스크를 `특정 시간` 또는 `주기적`으로 `실행`하게 하기 (`ScheduledThreadPoolExecutor`)
	- `ScheduledThreadPoolExecutor`의 `scheduleWithFixedDelay`(주어진 주기마다 계속 반복해 작업을 실행) 또는 `schedule`(주어진 지연 시간 이후 작업을 실행)를 사용
### 상황에 따른 실행자 서비스 사용법
- JDK에는 필요에 따라 `Callable`, `ExecutorService`, `ScheduledExecutorService`, `ThreadFactory`를 생성할 수 있는 `정적 팩터리`를 제공하는 유틸리티 클래스 [Executors](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html)가 존재함
#### 작업 큐를 둘 이상의 스레드가 처리해야 하는 경우
- `스레드 풀`([ThreadPoolExecutor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html)또는 [ScheduledThreadPoolExecutor](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledThreadPoolExecutor.html))이라고 부르는, "다른 종류의 `실행자 서비스`"를 생성하는 서로 다른 `정적 팩터리`를 호출하면 됨
	- `Executors`에 있는 `newFixedThreadPool(int nThreads)`, `newScheduledThreadPool(int corePoolSize)` 등의 `정적 팩터리`를 사용해 생성 가능
	- 각 `스레드 풀`의 `스레드 개수`는 `고정`하거나 `필요에 따라 증감`시킬 수 있음
		- 정확히는, `ThreadPoolExcecutor`는 `CorePoolSize`와 `maximumPoolSize`에 따라 `풀 크기의 경계`를 설정한 뒤 `자동으로 풀 크기를 조정`
			- 이때, `CorePoolSize`와 `maximumPoolSize`를 동일하게 설정하면 `고정 크기`의 `스레드 풀`이 됨
	- 이외에도 `ThreadPoolExecutor`는 스레드가 어느시간 이상 idle 상태면 종료시킬지(`setKeepAliveTime`), 실행 불가능한 태스크에 대한 핸들러는 무엇을 쓸지(`setRejectedExecutionHandler`) 등 `스레드 풀`의 `동작을 결정`하는 대부분의 `속성을 설정`할 수 있음
#### 실행자 서비스를 사용하기 까다로운 경우
- **작은 프로그램** 또는 **가벼운 서버**인 경우, 특별히 설정할 요소가 없고 범용성이 좋은 `Executors.newCachedThreadPool()`를 사용하는 것을 추천
- 반면, 무거운 `프로덕션 환경`에서는 이러한 `CachedThreadPool`이 좋지 않음
	- 참고로, 여기서 언급하는 `CachedThreadPool`은 실제 클래스 이름이 아니라 특정 구현 방식을 설명하기 위해 사용된 단어임
	```java
	// 실제 Executors 클래스 코드에서 CachedThreadPool을 만드는 코드
	public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>(),
                                      threadFactory);
    }
	```
- `CachedThreadPool`에는 요청받은 태스크들이 큐에 쌓이지 않고, `즉시 스레드에게 넘겨져 실행` 됨
	- 만약 `서버의 부하가 심해` CPU 사용률이 100%에 다다를 경우, *새로운 태스크가 도착할 때 마다* **새로운 스레드를 만들어 상황을 악화**시킴
- 따라서 `프로덕션 환경`에서는 `스레드 개수가 고정`된 `Executors.newFixedThreadPool`을 사용하거나, `ThreadPoolExecutor` 클래스를 `직접 사용`해 `최대한 통제`하는 것이 나음
#### 작업 큐를 직접 작성하거나, 스레드를 직접 다루지 말 것
- 만약 스레드를 직접 다룬다면, `Thread`가 `작업 단위`인 동시에 `실행 메커니즘`의 역할을 하기 때문
- 반면, `실행자 프레임워크`에서는 `작업 단위`와 `실행 메커니즘`이 **`분리`** 되어 있음
	- `작업 단위`를 나타내는 `핵심 추상` 개념이 `태스크`임
		- `태스크`에는 `Runnable`과 `Callable`이 있음
			- `Callable`이 `값을 반환`하고 `예외를 던질 수 있다`는 점을 제외하면 `Runnable`과 **비슷함**
	- 이러한 태스크를 `실행하는 메커니즘`이 `실행자 서비스`임
		- 이처럼 두 가지가 분리되어 있기 때문에, 필요에 따라 `태스크 실행 정책`을 선택하고 추후 변경할 수 있는 **`유연성`** 을 얻을 수 있음
- 종합하면, `실행자 프레임워크`는 *마치 `컬렉션 프레임워크`가 집계 작업을 담당하듯*, `작업 수행을 담당`한다는 것
### Java 7에 등장한 Fork-Join
- Java 7에서는 `실행자 프레임워크`가 *`포크-조인 풀(Fork-Join Pool)`로 알려진 특별한 종류의 `실행자 서비스`에 의해 실행되는* `포크-조인 태스크`를 지원하도록 **`확장`** 됨
- `포크-조인 태스크(ForkJoinTask)`의 인스턴스는 더 작은 `하위 태스크`로 `분할`될 수 있으며, `포크-조인 풀`을 구성하는 `스레드들`이 이러한 `태스크`들을 처리하고 (가능하다면)`다른 스레드의 남은 태스크`까지 가져와 `처리`함
	- 이를 통해 `CPU 사용률 향상`, `높은 처리량`, `낮은 지연 시간`을 달성할 수 있음
- 이러한 `포크-조인 태스크`를 작성하고 튜닝하는 것은 까다롭긴 하나, `포크-조인 풀`을 기반으로 작성된 `병렬 스트림`을 사용하면 적은 노력으로 위 성능 이점들을 얻을 수 있음 (다만, `포크-조인`에 `적합한 형태의 작업`일 경우에만 가능)

> 	Parallel streams utilize the fork/join framework for executing parallel tasks. This framework provides support for the thread management necessary to execute the substreams in parallel. The number of threads employed during parallel stream execution is dependent on the CPU cores in the computer.
> 	(병렬 스트림은 병렬 작업을 실행하기 위해 포크/조인 프레임워크를 활용합니다. 이 프레임워크는 하위 스트림을 병렬로 실행하는 데 필요한 스레드 관리를 지원합니다. 병렬 스트림 실행 중에 사용되는 스레드 수는 컴퓨터의 CPU 코어에 따라 달라집니다.)

출처: [Parallel streams in Java: Benchmarking and performance considerations](https://blogs.oracle.com/javamagazine/post/java-parallel-streams-performance-benchmark#:~:text=The%20partial%20results%20from%20the,framework%20for%20executing%20parallel%20tasks.)

