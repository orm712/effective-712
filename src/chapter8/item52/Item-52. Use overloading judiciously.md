다중정의는 신중히 사용하라
## 다중정의된 메서드는 컴파일 시간에 정해진다
### 예시
```java
// 컬렉션 분류기
public class CollectionClassifier {
	public static String classify(Set<?> s) {
		return "Set";
	}
	
	public static String classify(List<?> lst) {
		return "List";
	}
	
	public static String classify(Collection<?> c) {
		return "Unknown Collection";
	}
	
	public static void main(String[] args) {
	Collection<?>[] collections = {
		new HashSet<String>(),
		new ArrayList<BigInteger>(),
		new HashMap<String, String>().values()
	};
	for (Collection<?> c : collections)
		System.out.println(classify(c));
	}
}
```
- 위 코드를 보았을 때, `Set`, `list`, `Unknown Collection`를 차례대로 출력할 것 같지만, 실제로는 `Unknown Collection`만 3번 출력됨
	- `다중정의된 메서드들` 중 *어느 메서드가 호출될 지는* `컴파일타임에 정해지기 때문`
	- 위 코드에서, `for (Collection<?> c : collections)`에서의 `c`는 항상 `Collection<?>` 타입임
		- *런타임에는 타입이 달라지지만*, `메서드 선택`에는 `영향 X`
## 재정의는 동적으로, 다중정의는 정적으로 선택된다
```java
class Wine {
	String name() { return "wine"; }
}
class SparklingWine extends Wine {
	@Override String name() { return "sparkling wine"; }
}
class Champagne extends SparklingWine {
	@Override String name() { return "champagne"; }
}

public class Overriding {
	public static void main(String[] args) {
		List<Wine> wineList = List.of(
			new Wine(), new SparklingWine(), new Champagne());
		for (Wine wine : wineList)
			System.out.println(wine.name());
	}
}
```
- 메서드가 재정의된 경우, 해당 객체의 런타임 타입에 맞춰 메서드가 결정됨
	- 컴파일타임에 인스턴스의 타입이 어땠는지는 상관 없음
- 반면 `다중정의된 메서드`의 *인자로 주어진 객체의 `런타임 타입`* 은 **중요치 않음**
	- 매개변수의 `컴파일타임 타입`으로만 결정됨
```java
public static String classify(Collection<?> c) {
	return c instanceof Set ? "Set" :
		c instanceof List ? "List" : "Unknown Collection";
}
```
- 만약, *런타임 타입에 따라* 다른 결과를 내놓고 싶다면, 다음과 같이 `instanceof` 키워드를 통해 `명시적으로 검사`하는 방식으로 동작을 바꿔주면 해결됨
- 또한, 가능한 *헷갈릴 수 있는 다중정의 메서드*는 `작성하지 않는게 좋음`
	- 특히 `공개 API`일 경우, **API 사용자들**이 *어떤 다중정의 메서드가 호출될 지* `혼동`하기 쉽기 때문
### 다중정의의 대안
1. `매개변수 수가 다른` 다중정의 메서드 만들기
2. 다중정의 대신, `메서드 이름을 다르게` 지어주기
	- [ObjectOutputStream](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectOutputStream.html)의 `writeXXX` 메서드가 대표적인 예시
	- *`write` 라는 이름으로 다중정의 하는 대신*, `write+타입 명`(ex. `writeBoolean`, `writeInt`, ...) 과 같은 식으로 이름을 붙임
		- 이렇게 명명하면, 추가로 연관된 메서드의 이름 짝을 맞출 수 있음
		- [ObjectInputStream](https://docs.oracle.com/javase/8/docs/api/java/io/ObjectInputStream.html)의 `readBoolean`, `readInt` 등이 그 예시
## 생성자의 다중정의
- `생성자`는 *이름을 다르게 지을 수 없으므로*, `두 개 이상의 생성자`는 무조건 `다중정의`
- 다만, `생성자`는 *`재정의`와 혼용될 수 없고*, `정적 팩터리`라는 `대안`이 존재
### 그럼에도 생성자를 다중정의 한다면
- `매개변수 수가 같은` 다중정의 메서드들간에 `어떤 매개변수 집합을 처리할 지`를 확실히 `구분`시키면, **혼동을 일으킬 일이 없음**
	- 즉, 매개변수 집합들이 `"근본적으로 다르면(radically different)"` 됨
		- `근본적으로 다르다`는 것은, *(null이 아닌 값들에 대해)* `서로 간의 형변환이 불가능`하다는 뜻
	- 이를 충족하면, 매개변수들의 *`런타임 타입`만으로* `어느 메서드가 호출될 지 결정`됨
### 주의 - 오토박싱
#### 예시) Integer 타입들로 이뤄진 Set과 List
```java
public class SetList {
	public static void main(String[] args) {
		Set<Integer> set = new TreeSet<>();
		List<Integer> list = new ArrayList<>();
		for (int i = -3; i < 3; i++) {
			set.add(i);
			list.add(i);
		}
		for (int i = 0; i < 3; i++) {
			set.remove(i);
			list.remove(i);
		} 
		System.out.println(set + " " + list);
	}
}
```
- 위 코드에서 `int` 값을 `add` 메서드를 통해 **삽입**할 경우, `오토박싱`을 통해 `Integer로 형변환`되어 삽입됨
- 반면, `remove` 메서드를 통해 `일치하는 원소를 삭제`할 때는 **이렇게 동작하지 않음**
	- *Set과 달리* `List`에는 remove가 `remove(int)` 와 `remove(Object)` 로 `다중정의`되어 있기 때문
- 따라서 *오토박싱이 이루어지지 않고* `remove(int)`가 호출되어 `대응되는 index의 원소들`이 `삭제`됨
- 이러한 경우 `(Integer) i` 또는 `Integer.valueOf(i)` 와 같은 형태로 `명시적인 형변환`을 하면 해결됨
- `List`의 `remove`들은 *Java 4 이전까지는* `근본적으로 다른 타입`들을 매개변수로 받았으나, `제네릭`과 `오토박싱`의 등장으로 더는 `근본적으로 다르지 않게 됐음`
### 주의 - 람다
```java
// 1. Runnable을 인자로 받는 Thread의 생성자 Thread(Runnable target)
new Thread(System.out::println).start();

// 2. Callable을 인자로 받는 ExecutorService.submit(Callable<T> task)
ExecutorService exec = Executors.newCachedThreadPool();
exec.submit(System.out::println);
```
- *Java 8에 등장*한 `람다` 와 `메서드 참조` 도 다중정의에서 혼란을 일으킬 수 있음
- 두 코드에 `System.out::println`을 인자로 주면, *첫 번째 코드는 잘 동작하지만*, 두 번째 코드는 `컴파일 오류` 발생
	- 이는, `ExecutorService`의 `submit`이 다중정의되어, `Runnable` 뿐만 아니라 `Callable`도 받을 수 있기 때문
		- `Runnable`과 `Callable` 모두 스레드에 의해 실행될 수 있는 작업들을 나타내지만, `Runnable`은 `Thread` 또는 `ExecutorService`에 의해 실행될 수 있는 반면 `Callable`은 `ExecutorService`에 의해서만 가능
		- `Runnable.run()`은 `void`이지만, `Callable<T>.call()`는 `값을 반환`할 수 있음
- 하지만, `System.out`의, 정확히는 [PrintStream](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html#println--)의 모든 다중정의된 `println`은 `void`이기 때문에 헷갈릴 이유가 없다고 생각할 수 있음
	- 하지만 Java의 `다중정의 해소(Resolution) 알고리즘`은 이와 같이 동작하지 않음
		- 참조된 메서드(`println`)와 호출한 메서드(`submit`) `모두 다중정의`되었기 때문
			- 때문에, 만약 `println`가 단일 메서드였다면 이런일이 발생하지 않았음
		- 기술적으로, `System.out::println`은 `부정확한 메서드 참조(ineaxct method reference)`(또는 `암시적 타입 람다식(implicitly typed lambda)`)임
			- [부정확한 메서드 참조(ineaxct method reference)](https://docs.oracle.com/javase/specs/jls/se21/html/jls-15.html#jls-15.13.1): 목표 타입을 확인하기 전에 이미 `식이 충분히 모호`(어떤 메서드를 호출하려는지 알 수 없음)하기 때문에, 이러한 `식을 포함하는 인자`는 `적용 가능성(applicable) 테스트`(다중정의 된 메서드 중 *어떤 것이 주어진 인자로 올바르게 호출될 수 있는지* 확인하는 과정)에서 `제외`되며, *다중정의 해소가 완료될 때까지* `무시` 됨(예상되는 `항수(전달받는 인자 갯수)`는 여전히 다중정의 해소에 고려함)
			- 즉, `모호한 메서드 참조`가 주어지면, 컴파일러는 해당 `메서드 참조`를 임의로 무시하고 다른 정보들을 토대로 `다중정의 해소`를 시도함
- 따라서 메서드를 `다중정의`할 때, 서로 다른 `함수형 인터페이스`라도 `같은 위치의 인수`로 받아서는 `안 됨`
	- 즉, *함수형 인터페이스 간에* `근본적으로 다르지 않음`을 의미
	- 컴파일 시 `커맨드 라인 스위치`로 `-Xlint:overloads`를 설정하면 *이러한 종류의 다중정의*를 `경고`해줌
#### 근본적으로 다른 타입들
- `클래스 타입(Object 이외)`과 `배열 타입`은 근본적으로 다름
- *`Serializable`, `Cloneable`을 제외한*  `인터페이스 타입`과 `배열 타입`도 근본적으로 다름
- `두 클래스` 간에 *서로의 자손이 아닌 경우*, 두 클래스를 `'관련이 없는(unrelated)'` 클래스라고 부름
	- ex) `String`과 `Throwable`
- *어떤 객체도* 두 클래스의 `공통 인스턴스`가 **될 수 없으므로** `'관련이 없는' 클래스`들 끼리는 `근본적으로 다름`
- 이외에 서로간의 `형변환이 금지된(Forbidden Conversions)`([JLS, 5.1.12](https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.12)) 타입 쌍도 있음
## 이러한 지침을 깬 경우
- `Java Platform 라이브러리`에도 이번 장에 설명한 지침들을 어긴 케이스가 있음
### 예시 - String
- Java 4부터 `String`은 `contentEquals(StringBuffer)`라는 메서드가 있었음
- Java 5에서 [CharSequence](https://docs.oracle.com/javase/8/docs/api/java/lang/CharSequence.html)라는, `StringBuffer`, `StringBuilder`, `String`, `CharBuffer`와 같이 비슷한 타입들을 위한 `공통 인터페이스`가 등장
- 이에 따라, `String`에는 `CharSequence`를 인자로 받는 `contentEquals(CharSequence)`가 다중정의 됨
- 다만, 이 케이스의 경우 다중정의된 `두 메서드`가 `동일한 객체 참조`에서 호출될 때, `정확히 동일한 작업`을 `수행`하므로, 문제될 건 없음
  ```java
  public boolean contentEquals(StringBuffer sb) {
      return contentEquals((CharSequence) sb);
  }
  ```
	- 이처럼 `동일한 작업을 수행`하도록 하는 가장 일반적인 방법은, 위처럼 `더 특수한(더 좁은 범위의) 메서드` 측에서 `더 일반적인 메서드`로 일을 `전달(Forwarding)`하면 됨
- 반면, `String`의 `valueOf(char[])`와 `valueOf(Object)`의 경우와 같이 동일한 객체를 건네도 다른 일을 수행하는 케이스도 있음
# 핵심 정리
- 언어에서 `다중정의`를 `허용`한다는 것은, *꼭 활용하란 의미가 아님*
- 웬만해서는 `매개변수가 수 같으면` 다중정의를 `피하는게 좋음`
	- 불가능하다면, *헷갈릴만한 매개변수*는 `형변환`하여 `정확한 다중정의 메서드`가 선택될 수 있도록 해야함
		- ex) `새로운 인터페이스`를 구현하기 위해 기존 클래스를 수정할 경우, 모든 `다중정의 메서드`들이 *`동일한 객체`를 넘겼을 때* `동일하게 동작`하도록 해야 함
		- *만약 그렇지 못하면*, 다중정의된 메서드나 생성자를 효과적으로 사용하는데 실패함은 물론, 그 이유도 이해하지 못하게 됨