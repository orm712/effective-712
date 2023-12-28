# ITEM 44. 표준 함수형 인터페이스를 사용하라.

## 서론

람다를 지원하며 API를 작성하는 모범 사례도 크게 바뀌었다.

상위 클래스의 기본 메서드를 재정의해 원하는 동작을 구현하는 템플릿 메서드 패턴의 매력이 크게 줄어들었다.

이를 대체하는 현대적인 해법은 같은 효과의 함수 객체를 받는 정적 팩터리나 생성자를 제공하는 것이다.

### 람다가 없던 시절

```java
abstract class Worker {
	abstract void work();

	void dailyRoutine() {
		System.out.println("Wake up");
		work();
		System.out.println("Sleep");
	}
}

class Developer extends Worker {
	@Override
	void work() {
		System.out.println("Write code");
	}
}

public static void main(String[] args) {
	// 사용 예
	Worker dev = new Developer();
	dev.dailyRoutine();
}
```

### 람다 표현식 생긴 이후

```java
import java.util.function.Consumer;

class Worker {
	static void dailyRoutine(Consumer<Worker> work) {
		System.out.println("Wake up");
		work.accept(new Worker());
		System.out.println("Sleep");
	}
}

public static void main(String[] args) {
	// 사용 예
	Worker.dailyRoutine(worker -> System.out.println("Write code"));
}
```

    위 예시의 `Worker.dailyRoutine(worker -> System.out.println("Write code"));`는
    반환값 void, 매개변수 타입은 Comsumer<Worker> 입니다.

    worker -> System.out.println("Write code")는 람다 표현식입니다.
    이 람다 표현식은 Consumer<Worker> 인터페이스를 구현합니다. Consumer<Worker> 인터페이스는 void accept(T t)라는 하나의 추상 메소드를 가지고 있습니다. 여기서 T는 Worker 타입입니다.
    람다 표현식에서 worker는 Worker 타입의 매개변수이며, System.out.println("Write code")는 이 매개변수를 사용하는 본문입니다. 이 본문은 accept 메소드의 구현부에 해당합니다.

---

결론은, 함수 객체를 매개변수로 받는 생성자와 메서드를 더 많이 만들어야 한다는 것이다.
이때 함수형 매개변수 타입을 올바르게 선택해야 합니다.

LinkedHashMap을 생각해보자. 이 클래스의 protected메서드인 removeEldestEntry를 재정의하면 캐시로 사용 가능하다.
(실제 LinkedHashMap 보여주기)

맵에 새로운 키를 추가하는 put메소드를 호출하여 true 반환 시, 맵에서 가장 오래된 원소쌍을 제거한다.
예를 들어, 아래와 같이 재정의하면 맵에 원소는 100개가 될 때 까지만 커지고, 그 이후로는 새로운 키가 더해질 때 마다 삭제된다.

```java
protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
	return side() > 100;
}
```

이렇게 작성하면 잘 동작하지만, 람다를 사용하면 훨씬 잘 표현할 수 있다.
이 책의 저작자는, LinkedHashMap을 오늘날 다시 구현하면 함수 객체를 받는 정적 팩터리나 생성자를 제공했을 것이라고 한다.

위 예시의 메서드를 함수객체를 받게 변경하려면, Map.Entry<K,V> 를 받아서, boolean을 반환해야 할 것만 같다. 하지만 꼭 그렇지는 않다.

remove~메서드는 size()를 호출해 맵 안의 원소 수를 알아내는데, removeEldestEntry가 인스턴스 메서드라 가능하다.

하지만 생성자에 넘기는 함수 객체는 이 맵의 인스턴스 메서드가 아니다. size()등으로 크기를 확인할 수 없다.
생성자를 호출할 때는 맵의 인스턴스가 존재하지 않기 때문이다.
따라서 맵은 자기 자신도 함수 객체에 건네줘야 한다. 이 부분을 감안해서 함수형 인터페이스를 선언하면 아래와 같이 선언할 수 있다.

```java

@FunctionalInterface
interface EldestEntryRemovalFunction<K, V> {
	boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
}
```

위 인터페이스도 잘 동작하긴 하지만, 굳이 사용할 이유는 없다.

왜냐하면, 자바 표준 라이브러리에 이미 같은 모양의 인터페이스가 준비되어 있기 때문이다.
위의 초반 예시코드에서 나온 Consumer도 이미 자바 표준 라이브러리에 등록된 함수형 인터페이스이다.

이 자바 표준 라이브러리에 등록된 인터페이스들은, java.util.functional 패키지를 보면 다양한 용도의 표준 함수형 인터페이스가 담겨져 있다.

필요한 용도에 맞는게 이미 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 활용하라.
이렇게 하면, API가 다루는 개념의 수가 줄어들어 익히기 더 쉬워진다.

또한, 표준 함수형 인터페이스들은 유용한 디폴트 메서드들을 많이 제공하고 있으므로 다른 코드와의 상호운용성도 매우 좋아진다.

    예를 들어, Predicate 인터페이스는 predicate들을 조합하는 메서드를 제공한다.

앞의 LinkedHashMap예제에서는 직접 만든 `EldestEntryRemovalFunction`대신 `BiPredicate<Map<K,V>, Map.Entry<K,V>>` 를 이용할 수 있다.

`java.util.function`패키지에는 총 43개의 인터페이스가 담겨져 있다. 전부 기억하기 어렵겠지만, 기본 인터페이스 6개만 기억하면 나머지는 파생형이라 유추하기 쉽다.
이 기본 인터페이스는 모두 참조 타입 용이다. 레퍼런스 타입이 아닌, 프리미티브 타입은 사용할 수 없으므로 박싱하도록 하자. 여기서 박싱을 잘 이용하여 박싱과 언박싱에 쓸모없는 코스트를 낭비하지 않도록 잘
설계하는것이 좋다.

### 종류

1. Operator
    - 반환값과 인수의 타입이 같은 함수

    1. UnaryOperator
        - 인수가 1개인
    2. BinaryOperator
        - 인수가 2개인
2. Predicate
    - 인수 하나를 받아 Boolean을 반환하는 함수
3. Function
    - 인수와 반환 타입이 다른 함수
4. Supplier
    - 인수를 받지 않고 값을 반환(제공) 하는 함수
5. Consumer
    - 인수를 하나 받고, 반환값은 없는 (특히 인수를 소비하는 경우) 함수

인터페이스와 함수시그니쳐의 정리와 예시이다.

1. UnaryOperator<T>
    - `T apply(T t)`
    - `String::toLowerCase`
2. BinaryOperator<T>
    - `T apply(T t1, T t2)`
    - `BigInteger::add`
3. Predicate<T>
    - `boolean test(T t)`
    - `Collection::isEmpty`
4. Function<T,R>
    - `R apply(T t)`
    - `Arrays::asList`
5. Supplier<T>
    - `T get()`
    - `Instant::now`
6. Consumer<T>
    - `void accept(T t)`
    - `System.out::println`

위의 기본 인터페이스는 기본 타입인 int, long, double로 각각 3개씩 변형이 생긴다.
이름은 대충 기본이름앞에 타입이름을 붙인다. 예를들면 `IntPredicate, LongBinaryOperator`등

Function의 변형은 매개변수화 되고, 다른 인터페이스들과 다르게 추가 변형이 9개나 더 있다.
책에 주절주절 길게 설명중인데, 이걸 다 외우기엔 수도 너무 많고 규칙성도 부족하다. 찾아보기 어렵지 않은 편이기 때문에, 자주 사용해보면서 어떤 느낌인지 익히는게 좋다.

표준 함수형 인터페이스는 대부분 기본 타입만 지원한다. int, long등.. 박싱을 쓸 거면 잘 생각해서 사용하자.

---

이제 대부분 상황에서 직접 작성하는것보다 표준 함수형 인터페이스를 사용하는편이 나음을 알 수 있다.
그럼 대체 내가 직접 작성해야 할 때는 언제일까?
내가 필요한 용도에 맞는게 없다면 니가 만들어야지 별수있나 ㅇㅇ...

막 매개변수 3개를 받는 Predicate라던가 검사 예외를 던져야하거나 등 그런 경우. 직접 만들어야 겠지?


---

이때까지 자주 본 Comparator<T> 인터페이스를 떠올려보자. 구조적으로는 ToIntBiFunction<T,U>와 동일하다. 근데 왜 따로 만들었을까? 그 이유는 몇 가지 있다.

1. API에서 굉장히, 매우 많이 사용된다. 특히 이름이 뭐 하는놈인지 정말 잘 설명한다.
2. 구현하는 쪽에서 반드시 지켜야 할 규약을 담고 있다.
    - 이해가 안 되나?
        - 일관성 있는 비교: Comparator 인터페이스는 객체 간의 순서를 일관되게 비교하는 규약을 제공합니다. 예를 들어, compare(a, b)가 양수를 반환하면, compare(b, a)는 음수를
          반환해야 합니다.
        - 동치성 규약: 두 객체가 동일하다고 판단될 경우, compare 메서드는 0을 반환해야 합니다. 이는 정렬 알고리즘에서 중요한 부분이며, 일관된 정렬 결과를 보장하는 데 필수적입니다.
3. 비교자들을 변환하고 조합해주는 유용한 디폴트 메서드들을 듬뿍 담고 있다.
    - 이해가 안 되나?
        - 조합 및 변환 메서드: Comparator 인터페이스는 비교자를 조합하고 변환하는 다양한 디폴트 메서드를 제공합니다. 예를 들어 reversed() 메서드는 주어진 비교자의 순서를 반대로 하는
          새로운 비교자를 반환합니다.
        - 편의성 제공: 이러한 메서드들은 특히 복잡한 정렬 기준을 쉽게 구현할 때 유용합니다. 예를 들어, 여러 필드에 대한 정렬 기준을 조합할 때 thenComparing 메서드를 사용할 수
          있습니다.

만약, 네가 만들 함수형 인터페이스가 자바 기본 라이브러리에서 이미 제공하는 형태라서 안 만들어도 될려나? 싶을때 위의 Comparator가 나타내는 특성과 같이 3가지 중 하나이상 포함된다면 자바 라이브러리 말고
직접 만드는것을 진지하게 고민해야 한다.

- 자주 쓰이고, 이름 자체가 용도를 명확히 설명한다
- 반드시 따라야 하는 규약이 있다
- 유용한 디폴트 메서드를 제공할 수 있다.

---

전용 함수형 인터페이스를 니가 만들려고 생각했다면, 지금 만들려고 하는게 '인터페이스' 임을 명심해야 한다.
그냥 클래스, 단순한 이넘 등을 만드는것과는 비교할 수 없이 주의해서 설계하고 고민해야 한다. 아이템 21번의 내용을 확인하자.


> 핵심정리
>
> 이제 자바도 람다를 지원한다. 여러분도 이제 API를 설계할 때, 람다도 염두에 두어야 한다는 것이다.
> 입력값과 반환값에 함수형 인터페이스 타입을 적극 활용하라. 보통은 기본 자바 라이브러리의 표준 함수형 인터페이스를 사용하는것이 가장 좋은 선택이지만, 가끔 직접 만들어 사용하는게 나을수도 있는 경우를 확인하고,
> 고민해서 직접 만들어 사용하기도 해야한다.
