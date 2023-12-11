ordinal 인덱싱 대신 EnumMap을 사용하라.
## enum 타입의 index를 얻는 기존 방식
배열 또는 리스트에서 원소를 꺼낼 때, `ordinal()`을 통해 index를 얻는 방식을 종종 사용하곤 했다.
```java
class Plant {
	enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }
	final String name;
	final LifeCycle lifeCycle;
	Plant(String name, LifeCycle lifeCycle) {
		this.name = name;
		this.lifeCycle = lifeCycle;
	}
	@Override public String toString() {
		return name;
	}
}
```
예를 들어, 위와 같이 식물을 나타내는 클래스 `Plant`가 있다고 가정하자.
그리고 이를 식물의 생명주기인 `ANNUAL`, `PERENNIAL`, `BIENNIAL` 별로 Set을 만들고 식물들을 해당하는 생명주기의 Set에 넣도록 한다고 가정하자.
이때, 생명주기별 Set들의 집합의 index를 생명주기의 `ordinal()`을 이용하는 개발자도 있을 것이다.
## `ordinal()`을 index로 사용했을 때의 문제점
### 배열은 제네릭과 호환되지 않는다.
따라서, 비검사 형변환(unchecked cast)이 수반되고 컴파일도 깔끔하게 수행되지 않는다.
### 배열은 각 index의 의미를 알 수 없다.
따라서, 출력 결과에 직접 index 별 레이블을 달아줘야 한다.
### `ordinal()`이 정확한 정수를 사용한다는 걸 개발자가 보증해야 한다.
`int`는 enum처럼 타입 안정성을 제공하지 못하기 때문에, 잘못된 값을 사용하면 프로그램은 조용히 오작동하거나, `ArrayIndexOutOfBoundsException`을 던질것이다.
## 더 나은 해결책 - `EnumMap`
위 사례에서 사실상 배열과 ordinal은 enum 상수를 Key로, Set을 Value로 매핑하는 셈이다.
따라서 이는 Map으로 대체 가능하고, enum 타입을 Key로 사용하도록 설계된 `EnumMap`이 이미 존재한다.
## `EnumMap`의 장점
### 안전하지 않은 형변환(`unsafe case`)을 사용하지 않는다.
### 별도의 레이블을 달아 줄 필요가 없다.
enum 타입이 출력 가능한 문자열을 제공해주기 때문이다.
### index를 계산하는 과정에서의 오류 발생 가능성이 없다.
### `ordinal-indexed` 배열과 속도가 비슷하다.
이는 `EnumMap`이 내부에서 배열을 사용하기 때문이다. 
`EnumMap` 은 구현 세부를 개발자로부터 숨겨 Map의 타입 안정성과, 배열의 성능적 이점을 모두 챙길 수 있었다.
```java
public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V>  
    implements java.io.Serializable, Cloneable  
{  
    /**  
     * The {@code Class} object for the enum type of all the keys of this map.  
     *     * @serial  
     */  
    private final Class<K> keyType;  
  
    /**  
     * All of the values comprising K.  (Cached for performance.)     */    
	 private transient K[] keyUniverse;  
  
    /**  
	 * Array representation of this map.  The ith element is the value     * to which universe[i] is currently mapped, or null if it isn't     * mapped to anything, or NULL if it's mapped to null.     */    
	  private transient Object[] vals;
```
`EnumMap`을 생성할 때 key로 사용할 Class 객체를 인수로 받는데, 이는 **`한정적 타입 토큰(bounded type token)`** 으로, 런타임 제네릭 타입 정보를 제공한다.
## `Stream`을 사용해 코드 줄이기
```java
System.out.println(Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle,() -> new EnumMap<> Plant.LifeCycle.class), toSet())));
```
`Collectors.groupingBy()` 함수를 이용해, `mapFactory` 매개변수(두 번째 인수)에 원하는 맵 구현체를 명시할 수 있다.
이렇게 하면 `classifier`(첫 번째 인수)만 받는 `groupingBy`와 달리 `EnumMap`의 이점을 챙길 수 있다.
### `EnumMap`만 사용할 때와의 차이
Stream을 이용하면 **해당 Key를 갖는**, 즉 해당 생애주기에 속하는 **Plant가 있을 때만** 해당 생애주기에 대한 **중첩 맵을 만들게 된다**.
만약 생애주기 중 `ANNUAL`, `PERENNIAL`에만 속하는 식물들만 들어있는 배열을 사용한다면 Stream을 쓰면 2개의 맵만 만들어지고 EnumMap을 쓸 때는 3개의 맵 전부가 만들어진다.
## `ordinal`을 배열에서 두 번 쓰는 방식
매핑할 enum 타입이 2개 이상이라 배열에서 `ordinal`을 두 번 이상 사용하는 코드도 있다.
## 해당 방식의 단점
### 컴파일러가 ordinal - index 간의 관계를 알 수 없다.
즉, `Phase` 또는 `Transition`을 수정하면서 상전이표인 `TRANSITIONS`를 함께 수정하지 않으면 정상 동작하지 않을 것이다.
### 배열의 크기가 상태의 가짓수의 제곱으로 커진다
그리고 중간 중간 null값으로 대체되는 칸도 늘어나게 되어 공간 낭비가 심해진다.
## `EnumMap` 을 사용해 2개 이상의 enum을 매핑하는 방법
`EnumMap` 2개를 중첩하면 이러한 상태를 쉽게 구현할 수 있다.
```java
public enum Phase {  
    SOLID, LIQUID, GAS;  
    public enum Transition {  
        // 각 전이 상태들은 from, to 상태를 인자로 받아 생성된다.  
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),  
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),  
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);  
        private final Phase from;  
        private final Phase to;  
        Transition(Phase from, Phase to) {  
            this.from = from;  
            this.to = to;  
        }  
        // Initialize the phase transition map  
        // Transition의 from(Phase)을 Key로하고,  
        // [to(Phase)를 Key로 하고 Transition를 value로 받는 EnumMap]을 value로 받는 맵 m
        private static final Map<Phase, Map<Phase, Transition>>  
                m = Stream.of(values()).collect(Collectors.groupingBy(t -> t.from,  
                () -> new EnumMap<>(Phase.class),  
                Collectors.toMap(t -> t.to, t -> t,  
                        (x, y) -> y, () -> new EnumMap<>(Phase.class))));  
        public static Transition from(Phase from, Phase to) {  
            return m.get(from).get(to);  
        }}}
```
이때, `toMap`의 `MergeFunction(3번째 인자)`으로 들어간 `(x, y) -> y` 는 실제로 쓰이지 않는다.
우리가 `EnumMap`을 얻으려면, `mapFactory(4번째 인자)`를 지정해주어야 한다. 근데 `Collectors`에서는 `점층적 팩토리(telescoping factory)`형태로, 즉 받는 매개변수 만큼 팩토리 가짓수를 늘려놓은 형태로 `toMap`을 제공하고 있고, `mapFactory`를 인수로 받는 `toMap`은 `MergeFunction`까지 **같이 인자로 받기 때문에** 넘겨주는 것이다.
```java
public static <T, K, U, M extends Map<K, U>>  
Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,  
                         Function<? super T, ? extends U> valueMapper,  
                         BinaryOperator<U> mergeFunction,  
                         Supplier<M> mapFactory)
```
(이것 외에는 `toMap(KeyMapper, valueMapper)`, `toMap(KeyMapper, valueMapper, mergeFunction)`만 있다.)
### 새로운 원소 추가하기
새로운 Phase인 `PLASMA`와 그에 대응되는 Transition인 `IONIZE`, `DEIONIZE`를 추가한다고 해보자.
이중 `EnumMap`을 사용한다면, 단지 Phase에 `PLASMA`를 추가하고, Transition에 `IONIZE(GAS, PLASMA)`와 `DEIONIZE(PLASMA, GAS)`를 추가하면 끝이다.
만약 이전처럼 `ordinal`을 쓰는 2차원 배열을 사용하고 있었다면, Phase에 1개, Phase.Transition에 2개의 새로운 상수를 추가하고, 기존의 원소 9개 짜리 배열(3x3)을 16개 짜리 배열(4x4)로 교체해야 할 것이다.
그리고 그 과정에서 *원소 순서가 잘못*되거나 *적게 기입*하는 등 **실수**를 한다면 **런타임에서 문제**를 일으킬 것이다.

# 핵심
- 배열의 index를 얻고자 `ordinal`을 쓰는건 웬만해서 좋지 않으므로 `EnumMap`을 쓰자.
	- 다차원일 경우 `EnumMap<..., EnumMap<...>>`로 표현하자.
- 배열의 index"애플리케이션 개발자는 `Enum.ordinal`을 웬만해선 쓰지 말아야 한다."는 일반 원칙의 특별한 사례이다.