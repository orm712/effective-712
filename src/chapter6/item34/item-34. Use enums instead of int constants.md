int 상수 대신 열거 타입을 사용하라.
# 열거 타입(`enum type`)
열거 타입이란, 일정 개수의 상수 값을 정의한 다음 그 외의 값은 허용하지 않는 타입이다.
## 열거 타입 지원 이전의 코드 - 정수 열거 패턴(`int enum pattern`)
Java에서 열거 타입을 지원하기 전 까진, 정수 상수를 묶어 선언해 사용해왔다.
```java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```
## 정수 열거패턴의 단점
### 타입 안전을 보장할 수 없다.
위 예시에서 오렌지를 건네야 할 메서드에 사과를 보내고 `==`과 같은 비교 연산자로 비교해도 아무런 경고가 출력되지 않는다.
### 표현력이 좋지 않다.
위 예시에서 사과용 상수는 `APPLE_`로 시작하고, 오렌지용 상수는 `ORANGE_`로 시작하는 것 처럼 접두어를 사용해 이름 충돌을 방지해야 한다. Java에서 int enum 패턴을 위해 별도의 namespace를 지원하지 않기 때문이다.
### int enum 패턴을 사용한 프로그램은 깨지기 쉽다.
int enum 패턴은 상수들의 집합이고, 컴파일 하게 되면 그 값이 클라이언트들에게 그대로 전해진다.
따라서 상수의 값이 바뀐다면 오작동을 방지하기 위해 client도 반드시 재컴파일 해야 한다.
### 문자열로 출력하기 까다롭다.
값을 출력하거나, 디버거로 확인해도 숫자만 보여지고 의미있는 값을 확인할 수 없다.
### int enum 그룹 내 원소들을 알 수 없다.
또한, int enum 그룹 내의 수를 모두 순회하는 방법도 마땅치 않으며, 안에 몇 개의 상수가 존재하는 지도 알 수 없다.
## 변형 - 문자열 열거 패턴(`string enum pattern`)
정수 대신 문자열 상수를 사용하는 변형 패턴이다.
이는 더 나쁜(less desirable) 패턴으로, 의미를 출력할 순 있지만 상수의 이름을 사용하는 것 대신 문자열 값을 하드코딩하게 만든다.
하드코드 되어 있으므로 오탈자가 포함되어 있어도 컴파일러는 감지할 수 없고 따라서 런타임에 버그를 발생시킨다.
또한, 문자열 비교가 필요하므로 성능 저하 역시 뒤따른다.
## 이들의 대안, 열거 타입 (`enum type`) [Java docs](https://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.9)
Java의 열거 타입은 *단순한 int 값*인 **C, C++, C#과 같은 언어들의 것**과 달리 완전한 형태(full-fledged)의 클래스로 훨씬 강력하다.
이들은 클래스로 각 열거 상수 당 하나의 인스턴스를 만들어 `public static final` 필드로 공개(export)한다.
열거 타입은 접근 가능한 생성자를 제공하지 않아 사실상 final의 형태를 띄고, 따라서 열거 타입을 선언하므로써 만들어진 인스턴스들을 제외한 인스턴스는 존재할 수 없다. final의 형태를 띄므로 클라이언트가 이를 extends 하거나 인스턴스를 생성하는게 불가능하기 때문이다.
즉, 열거 타입들은 인스턴스 통제된다(instance-controlled).
따라서 싱글턴을 일반화한게 열거 타입이고, 싱글턴은 원소가 하나인 열거 타입이라고 할 수 있다.
## 정수 열거 패턴의 단점을 해소하는 열거 타입
### 컴파일-시간 `타입 안정성` 을 제공한다.
만약 메서드가 열거 타입을 인수로 받는다면, 인수로 받은 값은 null이 아니라면 해당 열거 타입의 값 중 하나임이 확실하다.
다른 타입의 값을 넘기려면 컴파일 오류가 발생한다.
### 각자의 namespace가 존재한다.
따라서 이름이 같은 상수도 공존할 수 있다.
### 열거 타입의 값이 바뀌어도 재컴파일이 필요하지 않다.
필드의 이름만 공개되어, 상수 값이 클라이언트에 컴파일되어 새겨지지 않기 때문이다.
### 문자열로 출력하기 쉽다.
열거 타입의 `toString`을 활용하면 출력하기 적합한 문자열을 제공해준다.
### 같은 enum type 그룹 순회가 가능하다.
`enumType.values()` 함수를 호출하면 해당 enum type의 모든 상수들을 순회할 수 있다.
## 열거 타입의 추가적인 장점
### 추가적인 메서드 또는 필드를 추가할 수 있다.
### 임의의 인터페이스를 implements 하게 할 수 있다.
추가로 이미 `Object`의 메서드들을 높은 품질로 구현해놓았고, `Comparable`과 `Serializable`을 구현해놓았다.
또한, 직렬화한 형태도 웬만한 변형에도 정상 동작하도록 디자인 되어 있다.
## 열거 타입에 메서드/필드 추가하기
상수의 모음으로 사용하긴 하지만, enum 타입도 사실상 클래스이므로 추상화를 완벽히 표현 가능하다.
만약 enum 상수와 값을 연결지으려면, 인스턴스 필드를 명시한 뒤 그들을 인자로 받고 필드에 저장하는 생성자를 만들면 된다.
즉, 생성자를 활용해 각 열거 타입이 여러 개의 값을 가질 수 있게 할 수 있다. 
또한 메서드를 통해 그 값들을 제공하게 할 수도 있다.
만약 열거 타입에서 상수를 제거한다면, *정수 열거 패턴과 달리* 해당 상수를 참조하는 줄에서 **디버깅시 유용한 메시지를 담은 컴파일 오류**를 발생시킨다.
일반 클래스처럼, 열거 타입에서 유용한 기능을 구현할 것들을 노출할 필요가 없다면 private 또는 package-private로 선언하자.
## 범용성에 따른 열거 타입 생성 위치
범용성이 높은 열거 타입은 Top-level 클래스로 만들고, 특정 클래스에서만 쓰인다면 해당 클래스의 멤버 클래스로 만든다.
### 예시 - `RoundingMode`
'소수 자릿수의 반올림 모드'를 뜻하는 `java.math.RoundingMode`는 `BigDecimal` 에서도 사용되지만, 관련 없는 영역에서도 유용한 추상화를 제공하므로 Top-level로 설계되어있다.
이를 통해 API간의 일관성을 높이는 것을 장려하고자 했다.
## 상수마다 동작이 달라져야 하는 경우
예시로 계산기의 연산 종류를 열거 타입으로 선언하고 해당 연산을 열거 타입 상수가 직접 수행하고자 한다면 어떻게 해야할까?
### switch문을 사용하는 경우
```java
public enum Operation {
	PLUS, MINUS, TIMES, DIVIDE;
	// Do the arithmetic operation represented by this constant
	public double apply(double x, double y) {
		switch(this) {
			case PLUS: return x + y;
			case MINUS: return x - y;
			case TIMES: return x * y;
			case DIVIDE: return x / y;
		} 
		throw new AssertionError("Unknown op: " + this);
	}
}
```
이 코드는 잘 동작하긴 하지만, 여러 좋지않은 점이 산재해있다.
1) 마지막의 `throw`문은 도달할 일이 없지만 기술적으로 도달 가능하므로 생략하면 컴파일되지 않는다. [#]([Chapter 14. Blocks and Statements (oracle.com)](https://docs.oracle.com/javase/specs/jls/se8/html/jls-14.html#jls-14.21))
2) 새로운 상수가 추가되면, 그에 맞는 case 문이 추가되어야 한다. 이를 빼먹으면 컴파일은 되지만 연산을 수행할 때 런타임 오류가 발생한다.
### 더 나은 방법 - `apply()`
```java
// 상수 특정 메서드를 적용한 사례
public enum Operation {
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
	Operation(String symbol) { this.symbol = symbol; }
	@Override public String toString() { return symbol; }
	public abstract double apply(double x, double y);
}
```
열거 타입 내에 `apply()` 라는 추상 메서드를 선언 한 뒤, 각 상수에서 그에 맞게 메서드를 재정의 하는 **`상수별 메서드 구현(constant-specific method implementation)`** 방식을 사용한다.
추가로, 각 상수 별 심볼을 받아 toString으로 이를 반환하도록 하면 계산식 출력도 편하게 할 수 있다.
### 문자열을 enum 타입 상수로 변환하는 방법
1. `valueOf(String)`을 사용한다
enum 타입에는 문자열 이름에 해당하는 상수를 반환해주는 메서드가 자동 생성된다.
2. `fromString(String)`을 구현한다.
`stringToEnum`는 열거 타입 상수 생성 후 정적필드가 초기화 될 때 값들이 들어선다.
예시처럼 `Stream`을 쓰는 것 대신 `Map`을 써도 되지만 enum 타입 상수는 생성시 자신의 인스턴스를 맵에 추가할 수 없다.
*상수 변수를 제외하고*는 생성자는 enum 타입의 정적 필드에 접근할 수 없다. 또한, enum 타입의 한 생성자에선 같은 enum 타입의 다른 상수에 접근할 수 없다.
### 상수별 메서드 구현은 enum 타입 상수간 코드 공유가 힘들다.
만약 급여명세서에서 쓸 요일을 enum 타입으로 구현한다고 하자.
이 때, 수당을 계산하는 메서드를 짜야 한다면 ① 일자 상수 별로 계산 메서드를 중복해서 넣거나, ② 평일/주말별 계산해주는 helper 메서드를 작성하는 방법이 있다.
두 방법 모두 코드가 길어져 가독성이 낮고, 에러 가능성이 높아진다.
계산 메서드를 평일용으로 구현 해놓고, 주말 상수에서 이를 재정의하도록 할 수도 있지만, 새로운 상수를 추가할 때 이를 재정의하지 않으면 평일용으로 구현한 코드를 그대로 물려받는 단점이 있다.
### 대안 - 중첩 enum 타입
깔끔하게 사용자로 부터 어떤 전략을 선택할 지 선택권을 줄 수 있다.
잔업 수당 계산 로직을 중첩된 enum 타입으로 옮기고, 상위 enum 타입 생성자에서 이를 선택하도록 한다.
이러한 방식을 사용하면 switch문을 추가하거나, 상수별 메서드 구현을 추가하는 번거로움이 줄어든다.
### switch 문을 이롭게 사용할 수 있는 경우
그럼에도 `switch`문은 enum 타입에 **상수별 동작을 혼합할때** 유용하다.
'주어진 연산의 반대 연산을 구하는 함수'처럼 *가끔 쓰이지만* enum 타입 내에 **포함시킬만큼 유용하지 않은 기능** 같은 것들을 이러한 방식으로 구현하면 좋다.
# enum 타입을 써야하는 경우
enum 타입은 정수 상수와 성능적 차이가 크게 두드러지지 않는다.
### 컴파일 시간에 멤버를 알 수 있는 상수들의 집합이라면, 항상 enum 타입을 사용하자.
메뉴 아이템, 연산 코드 등의 값들을 컴파일 타임에 이미 알고 있다면 사용 가능하다.
### enum 타입에 정의된 상수의 가짓 수가 고정될 필요는 없다.
enum 타입은 추후 상수가 추가되어도 Binary 수준에서 호환되도록 설계되어 있다.
# 요약
- enum 타입은 int 상수보다 더 읽기 쉽고, 안전하고, 강력하다.
- enum 타입에서 상수를 특정 데이터와 연결하거나, 상수 마다 다른 동작을 수행해야 한다면 생성자 또는 메서드가 필요하다.
	- 이러한 enum 타입에서는 `상수별 메서드 구현`을 사용하자.
- enum 타입 상수 일부가 같은 동작을 공유하면, `전략 열거 타입 패턴`을 사용하자.
	- 중첩 enum 타입을 선언해 사용하는 방법.