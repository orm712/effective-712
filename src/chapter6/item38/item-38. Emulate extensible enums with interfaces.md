확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라.
`enum` 타입은 타입 안전 열거 패턴(`typesafe enum pattern`) 보다 웬만한 상황에서 우수하다.
## 타입 안전 열거 패턴(`typesafe enum pattern`)
Effective Java 초판에 소개된 패턴.
Java 1.5에서 `enum` 타입이 공식적으로 지원되기 전에 사용된 패턴으로, `정수 열거 패턴`의 단점들을 대체하기 위해 클래스-기반 대안으로 제시 되었다.
`typesafe enum pattern`의 **예시**로는 아래의 카드 슈트(클럽, 다이아몬드, 하트, 스페이드) 예시가 있다.
```java
public class Suit {
    private final String name;

    public static final Suit CLUBS = new Suit("clubs");
    public static final Suit DIAMONDS = new Suit("diamonds");
    public static final Suit HEARTS = new Suit("hearts");
    public static final Suit SPADES = new Suit("spades");    

    private Suit(String name){
        this.name =name;
    }
    public String toString(){
        return name;
    }
}
```
_Effective Java Programming Language Guide_ 의 item 21에서 몇 가지 단점과 함께 소개되었는데 그 단점은 다음과 같다.
## 타입 안전 열거 패턴의 단점
### 하나의 집합으로 묶는것이 어색하다.
### 열거 상수를 `switch`문에서 사용할 수 없다.
아래와 같이 switch 문을 통해 카드 슈트가 무엇인지에 따라 다른 문자열을 출력하고 싶다고 하자.
```java
Suit suit = Suit.DIAMONDS;
switch (suit)
{
   case Suit.CLUBS   : System.out.println("clubs"); break;
   case Suit.DIAMONDS: System.out.println("diamonds"); break;
   case Suit.HEARTS  : System.out.println("hearts"); break;
   case Suit.SPADES  : System.out.println("spades");
}
```
이때, 자바 컴파일러는 `Suit.CLUBS` 라는 구문을 보고 상수 식(constant expression)이 필요하다는 오류를 출력할 것이다.
그렇다고 `Suit.` 라는 namespace를 뗀다면, 자바 컴파일러는 `CLUBS` 라는 키워드를 만난 뒤 알 수 없는 Symbol이라고 오류를 출력할 것이다.
이를 해결하고자 `CLUBS`를 정적 임포트 해도 `switch(suit)` 에서 `Suit`를 `int`로 바꿀 수 없다고 에러를 보고할 것이다.
## enum 타입의 단점 - 확장 불가능
`타입 안전 열거 타입`은 *위의 단점들에도 불구*하고, enum 타입보다 나은 점이 있는데 바로 **`확장이 가능`** 하다는 것이다.
즉, `타입 안전 열거 타입`은 다른 enum 값들을 가져온 뒤 값을 추가해 다른 목적으로 사용 가능하지만, enum 타입은 불가능하다는 것이다.
### enum 타입의 확장이 꼭 필요한가?
사실 대부분의 상황에서 enum 타입을 확장하는 것은 좋지 않다.
1. `확장한 타입의 원소`는 `기반 타입의 원소`로 **취급**할 수 있지만, **역은 성립하지 않는** 이상한 상황이 발생하기 때문이다.
2. `기반 타입`과 `확장된 타입`의 원소 **모두를 순회할 좋은 방법**도 마땅치 않다.
3. **확장성**을 높이고자 하면 `설계` 및 `구현`의 여러 측면이 **복잡**해진다.
## enum 타입의 확장이 쓸모있는 경우
이러한 단점에도, enum 타입을 확장하는게 이로운 경우가 있는데 바로 **`연산 코드(opcode)`** 이다.
### 연산 코드(`operation code`, `opcode`)
연산 코드의 각 원소는 특정 기계가 수행하는 연산을 뜻한다.
- item 34에서 계산기의 Operation을 `enum` 타입으로 만든 것이 그 예시이다.
연산 코드를 만들 때, *API가 제공하는 기본 연산 외*에도 사용자 확장 연산을 추가할 수 있도록 해주어야 할 때가 있다.
## enum 타입을 확장하는 방법 - `interface`
이러한 상황에서, enum 타입으로 유사한 효과를 낼 수 있는 방법이 있는데 바로 **`interface`** 를 이용하는 것이다.
enum 타입은 *`interface`를 `implement` 할 수 있기* 때문에, 연산 코드용 인터페이스를 먼저 정의한 뒤 enum 타입이 이를 *`implement`* 하도록 한다.
이렇게 하면 enum 타입이 *interface의* **표준 구현체** 역할을 한다.
`enum` 타입은 확장할 수 없지만, `interface`는 확장할 수 있기 때문에 `interface`를 **API의 연산의 타입**으로 사용하면 된다.
이 방식을 사용하면, enum 타입(A)이 *`implement`* 하고 있는 interface를 *`implement`* 한 *또다른 enum 타입(B)* 을 만든 뒤 **기존의 enum 타입(A)을 대체** 할 수 있다.
```java
public interface Operation {
	double apply(double x, double y);
}
public enum BasicOperation implements Operation {
	PLUS("+") {
	public double apply(double x, double y) { return x + y; }
	},
	MINUS("-") {
	public double apply(double x, double y) { return x - y; }
	},
	TIMES("*") {
	public double apply(double x, double y) { return x * y; }
	},
	DIVIDE("/") {
	public double apply(double x, double y) { return x / y; }
	};
	private final String symbol;
	BasicOperation(String symbol) {
	this.symbol = symbol;
	}
	@Override public String toString() {
	return symbol;
	}
}
```
기존의 타입(A) 인 `BasicOperation` 이 있고, `Operation`을 확장해 지수 연산, 나머지 연산을 추가하고자 한다.
이렇다면 `Operation`을 *`implement`* 한 enum 타입을 작성하면, `Operation`을 인수로 받는 어느 곳이든 사용 가능하다.
### enum 타입 자체를 인수로 넘기기
*단일 인스턴스* 뿐만 아니라 **enum 타입 전체** 역시 `기본 enum 타입`이 예상되는 곳에 `확장 enum 타입`을 인수로 넘길 수 있다.
이를 통해 `기본 enum 타입`의 요소에 더하여 사용 하거나, 대체해 `확장 enum 타입`을 사용할 수 있다.
1. **`Class 객체`** 를 넘기는 방법
```java
public static <T extends Enum<T> & Operation> void test(Class<T> opEnumType, double x, double y)
```
위와 같은 형태로, **`런타입 제네릭 타입`** 을 지정할 **`Class 리터털`** 을 인수로 넘겨받는다.
2. **`한정적 와일드카드 타입`** 인 `Collection<? extends Operation>`을 넘기는 방법
```java
public static void test(Collection<? extends Operation> opSet, double x, double y) 
```
이를 사용하면, *여러 구현체들*을 **조합한 집합**을 test의 인수로 넘길 수 있게 된다.
다만, `EnumSet`, `EnumMap`을 특정 연산에서는 사용할 수 없게 된다.
## `interface`를 이용한 확장의 단점
### enum 타입 간 구현 내용을 상속할 수 없다.
만약 해당 구현 내용이 *아무 state에도 의존하지 않는다면*, `interface`에 **디폴트 구현**으로 추가할 수 있다.
만약, enum 타입 간 *공유하는 기능이 많다면* 그 기능들을 별도의 `도우미 클래스`, `정적 도우미 메서드`로 **분리**한다면 코드 중복을 없앨 수 있다.
## Java Library에서 이를 활용한 예시
### java.nio.file.LinkOption
해당 enum 타입은 `CopyOption` 과 `OpenOption` interface를 *`implement`* 했다.
```java
public enum LinkOption implements OpenOption, CopyOption {  
  NOFOLLOW_LINKS;  
}
```
# 핵심
- enum 타입 자체를 확장할 순 없지만, `interface`와 interface를 *`implement`* 하는 `기본 열거 타입(basic enum type)`을 함께 사용해 같은 효과를 낼 수 있다.
	- 이를 통해 클라이언트는 **자신만의 `enum  타입`**(*또는 다른 타입*)을 만들 수 있다.
- API가 `interface 기반`으로 작성되었다면, `기본 열거 타입의 인스턴스`가 쓰이는 모든 곳을 `확장한 열거 타입의 인스턴스`로 대체할 수 있다.