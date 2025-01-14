`equals`는 일반 규약을 지켜 재정의하라
## `equals` 재정의의 위험성
- `Object`의 `equals`는 재정의하기 쉬워보이나 `주의할 점이 많음`
- 따라서, 아예 `재정의하지 않는 것`도 하나의 방법임
	- 이 경우, `해당 클래스의 인스턴스`는 `자기 자신`과만 **같음**
### 재정의하지 않는 것이 좋은 경우
#### 각 인스턴스가 본질적으로 고유한 경우
- 주로 `동작하는 객체`를 표현하는 클래스가 위 경우에 해당
	- ex) `Thread`
#### 인스턴스의 "논리적 동치성(logical equality)"를 검사할 필요가 없는 경우
- `Enum`이나, `값이 같은 인스턴스`가 *둘 이상 만들어지지 않음을 보장*하는 `인스턴스 통제 클래스` 같은 것이 위 경우에 해당
	- 위 경우, `논리적으로 같은 인스턴스`가 *공존하지 않으므로* "논리적 동치성 ≈ 객체 식별성"이 됨
- 클라이언트가 이러한 방식을 원하지 않거나, 필요하지 않다고 판단되는 경우 설계자는 `Object`의 `equals` 만으로도 해결할 수 있음
#### 상위 클래스에서 equals를 재정의했고, 이것이 현재 클래스에도 알맞은 경우
- 대부분의 `Set`, `List`, `Map`이 각각 `AbstractSet`, `AbstractList`, `AbstractMap`으로부터 상속받은 `equals`를 사용하는 것이 대표적인 예
#### 클래스가 private 또는 package-private 이고, equals가 호출될 일이 없는 경우
```java
@Override public boolean equals(Object o) {
	throw new AssertionError(); // Method is never called
}
```
- 만약 `equals`의 호출을 확실히 막고싶다면, 위처럼 구현하면 됨
### 재정의해야 하는 경우
#### "논리적 동치성"을 확인해야 하는데, 상위 클래스의 `equals`가 이를 지원하지 않는 경우
- `Integer`, `String`과 같이 값을 표현하는 `값 클래스`가 주로 이러한 경우에 해당함
## equals 재정의하기
### equals 일반 규약 
참고 - [Object (Java Platform SE 8 )](https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-)
> - 반사성(_reflexive_) : *null이 아닌* 모든 참조 값 x에 대해, `x.equals(x)`는 `true`이다.
> - 대칭성(_symmetric_) : *null이 아닌* 모든 참조 값 x, y에 대해, `x.equals(y)`가 `true`면 `y.equals(x)`도 `true`이다.
> - 추이성(_transitive_): *null이 아닌* 모든 참조 값 x, y, z에 대해, `x.equals(y)`가 `true`이고 `y.equals(z)`가 `true`이면 `x.equals(z)`도 `true`이다.
> - 일관성(_consistency_) : *null이 아닌* 모든 참조 값 x, y에 대해, `x.equals(y)`를 여러 번 호출할 경우, *객체의 같음을 비교하는데 사용한 정보가 수정되지 않은 한* 일관되게 `true` 를 반환하거나 일관되게 `false`를 반환한다.
> - *null이 아닌* 모든 참조 값 x에 대해, `x.equals(null)`은 `false`이다.

- 이러한 보편적인 규약을 지키는 것은 매우 중요함
	- 클래스의 인스턴스는 여러 곳에 전달되고, 전달받은 인스턴스를 사용하는 "*컬렉션 클래스를 포함한* 대부분의 클래스들"은 `전달된 객체`가 이러한 `규약을 지킬 것이라 가정`하고 동작하기 때문
### equals 규약 알아보기
#### 동치관계
- Object 명세에서 말하는 `동치관계`란, *간단히 말하면* 집합을 서로 동등한 요소들로 이뤄진 부분 집합으로 분할하는 연산
	- 이러한 부분 집합을 `동치류(equivalence classes)`라고 함
- `equals` 메서드가 유용한 역할을 하기 위해서는 `동치류에 속한 모든 요소`가 *같은 동치류에 속한* `어떤 요소와도 교환 가능`해야 함
#### 반사성
- 객체가 `자기 자신과 같아야 함`을 뜻함
- 이를 어긴다면, 인스턴스를 컬렉션에 넣고 `contains` 메서드를 호출할 경우 방금 넣은 인스턴스가 없다고 답하는 등의 오동작이 일어남
#### 대칭성
- 두 객체가 `서로에 대한 동치 여부`에 대해 `똑같이 답해야 함`을 뜻함
- 후술할 케이스처럼 두 클래스 중 `한 측`에서만 `동치 관계`를 지원하려 하는 경우 문제가 발생함
##### 예시 -  CaseInsensitiveString
```java
public class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성 위배
    @Override public boolean equals(Object o) {
        // 주어진 o가 CaseInsensitiveString 인스턴스인 경우
        if (o instanceof CaseInsensitiveString)
            // 해당 인스턴스의 s와 현재 인스턴스의 s를 대소문자 상관없이 비교
            return s.equalsIgnoreCase(
                    ((CaseInsensitiveString) o).s);
        if (o instanceof String) // 단방향 상호 운용성
            return s.equalsIgnoreCase((String) o);
        return false;
    }
	public static void main(String[] args) {  
		CaseInsensitiveString cis = new CaseInsensitiveString("Polish");  
		String s = "polish";  
		  
		System.out.println(cis.equals(s)); // true  
		System.out.println(s.equals(cis)); // false  
		  
		List<CaseInsensitiveString> list = new ArrayList<>();  
		list.add(cis);  
		  
		System.out.println(list.contains(s)); // false
	}
}


```
- `대소문자`를 `구별하지 않는` 클래스인 `CaseInsensitiveString`의 `equals`를 구현하려 함
- 같은 `CaseInsensitiveString`간의 `equals` 비교는 정상적으로 동작하나, `equals` 인자로 `String`을 받는 것을 허용하는 순간 **`대칭성이 붕괴`** 하게 됨
	- 위 경우, `cis.equals(s)`는 `true`를 반환하겠지만  `s.equals(cis)`는 `false`를 반환
- 컬렉션의 경우에도 `contains` 메서드를 수행할 때, 구현에 따라 `true`나 `런타임 예외`가 발생할 수도 있음
	- 즉, `equals 규약`을 어기면 *해당 객체를 사용하는* `다른 객체들`이 `어떻게 동작할 지` **`알 수 없음!`**
- 위 케이스에서는 `String`과의 연동성을 포기하면 해결됨
#### 추이성
- `첫 번째 객체(x)`가 `두 번째 객체(y)`와 같고, `두 번째 객체`가 `세 번째 객체(z)`와 같을 경우, `첫 번째 객체`와 `세 번째 객체`도 `같아야 함`을 뜻함
- 후술할 케이스처럼 `equals 연산에 영향`을 줄 수 있는, *`상위 클래스`에 없는* `새로운 필드를 추가`하는 경우 문제가 발생함
	- 자바 라이브러리에도 이러한 방식으로 구현된 클래스가 있는데, 그 중 하나가 [java.sql.Timestamp](https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html)임
		- [java.util.Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html)를 확장하되, `nanoseconds` 필드를 추가한 클래스로, `Timestamp`의 `equals` 구현은 `대칭성을 위반`하며 *`Timestamp` 객체와 `Date` 객체를 한 컬렉션 안에서 사용*하거나 다른 방식으로 *섞어 쓸 경우* `비정상적인 동작`을 할 수 있음
			- 따라서 `Timestamp` API 설명에서도 `Date`와의 혼용시 주의 사항을 언급하고 있음
			- 이러한 설계는 실수이니 절대 따라 해서는 안 됨
			
>  이 유형은 `java.util.Date`와 별도의 나노초 값의 복합체(Composite) 입니다. `java.util.Date` 컴포넌트에는 정수 초만 저장됩니다. 분수 초, 즉 나노 단위는 별도로 저장됩니다. 날짜의 나노 단위 구성 요소를 알 수 없기 때문에 `java.sql.Timestamp`의 인스턴스가 아닌 객체를 전달할 때 `Timestamp.equals(Object)` 메서드는 절대로 참을 반환하지 않습니다. 따라서 `Timestamp.equals(Object)` 메서드는 `java.util.Date.equals(Object)` 메서드와 대칭이 되지 않습니다. 또한 `hashCode` 메서드는 기본 `java.util.Date` 구현을 사용하므로 계산에 나노를 포함하지 않습니다.  
> (This type is a composite of a `java.util.Date` and a separate nanoseconds value. Only integral seconds are stored in the `java.util.Date` component. The fractional seconds - the nanos - are separate. The `Timestamp.equals(Object)` method never returns `true` when passed an object that isn't an instance of `java.sql.Timestamp`, because the nanos component of a date is unknown. As a result, the `Timestamp.equals(Object)` method is not symmetric with respect to the `java.util.Date.equals(Object)` method. Also, the `hashCode` method uses the underlying `java.util.Date` implementation and therefore does not include nanos in its computation.)  
> 참고: [Timestamp (Java Platform SE 8 )](https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html)

##### 예시 - ColorPoint
```java
// x, y 좌표 값을 갖는 클래스 Point
class Point {
	private final int x;
	private final int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof Point))
			// 주어진 o가 Point 인스턴스가 아닌 경우 무조건 false
			return false;
		Point p = (Point) o;
		return p.x == x && p.y == y;
	}
}

// Point 클래스에 색상(color)를 추가해 확장한 클래스 ColorPoint
class ColorPoint extends Point {
	private final Color color;

	public ColorPoint(int x, int y, Color color) {
		super(x, y);
		this.color = color;
	}
}
```
- 위 경우, `ColorPoint`간의 비교는 `Point`의 `equals`가 상속되어 사용되므로 *`색상 정보`를 무시*한 채 `비교 연산`을 수행
	- 따라서 색상 정보를 포함하도록 `equals`를 재정의해야함
		```java
		// 대칭성 위배!
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ColorPoint))
				return false;
			return super.equals(o) && ((ColorPoint) o).color == color;
		}
		```
	- 위처럼 `equals`를 재정의한다면 `Point`와 `ColorPoint`를 비교한 결과와 서로 바꾸어 비교한 결과가 다를 수 있음
		- `Point를 기준`으로 `equals`를 수행한다면 `색상 정보를 무시`할 것이고, `ColorPoint를 기준`으로 `equals`를 수행한다면 *전달받은 객체의 클래스 종류가 다르다*고 `false를 반환`할 것이기 때문
- 이를 반영해 `ColorPoint`를 기준으로 `Point`와 비교시 `색상 정보`를 무시한다면?
	```java
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point))
			return false;
		
		// o가 일반 Point라면, 색상 정보를 무시한 채 비교
		if (!(o instanceof ColorPoint))
			return o.equals(this);
		
		// o가 ColorPoint라면, 색상 정보를 포함해 비교
		return super.equals(o) && ((ColorPoint) o).color == color;
	}
	```
	- 위 경우, `대칭성`을 지켜냈지만 `추이성`이 깨져버린다.
		- 다음과 같이 `ColorPoint` 인스턴스 2개(`cp1`, `cp2`)와 `Point` 인스턴스 하나(`p1`)가 있다고 가정
		```java
			ColorPoint cp1 = new ColorPoint(1, 2, Color.RED);
			Point p1 = new Point(1, 2);
			ColorPoint cp2 = new ColorPoint(1, 2, Color.BLUE);
		```
		- 이때, `cp1`과 `p1` 의 비교, `p1`과 `cp2`의 비교는 `true`를 반환하나, `cp1`과 `cp2`의 비교는 `false`를 반환하게 됨
	- 또한, 이는 `무한 재귀`를 일으킬 수도 있음
		- 똑같은 방식으로 `equals`를 구현한 Point의 또 다른 하위 클래스 `SmellPoint`를 구현했다고 가정
		- 이후 `colorPoint.equals(smellPoint)`를 수행할 경우, `if (!(o instanceof ColorPoint/SmellPoint))`라는 조건에 걸려버려 `o.equals(this)`를 호출하게 될 것이고 서로 계속 같은 조건에서 걸려 `o.equals(this)`를 무한히 호출하게 됨
- 이러한 케이스는 근본적으로 모든 `객체 지향 언어`의 `동치관계`에서 발생하는 문제
	- 즉, `구체 클래스(instantiable class)`를 확장해 **새로운 값을 추가**하면서, **`equals 규약`을 만족** 시킬 방법은 **존재하지 않음**
		- *`객체 지향 추상화`의 이점을 포기하지 않는 한* 이는 불가능
			- 그렇다고 `equals` 안에서 `instanceof 검사` 대신 `getClass 검사`를 사용하면 *규약도 지키고 구체 클래스의 확장도 가능하다*고 이해하면 **안됨**
			```java
			@Override 
			public boolean equals(Object o) {
				if (o == null || o.getClass() != getClass())
					return false;
				Point p = (Point) o;
				return p.x == x && p.y == y;
			}
			```
			- 왜냐하면, 이는 `리스코프 치환 원칙(Liskov Substitution Principle, LSP)`을 **명백히 위반**하기 때문
				- `Point를 상속한 하위 클래스`는 `Point`로 간주되어 어디서든 `Point`로써 활용 가능해야 함
					- `리스코프 치환 원칙`에 따르면, `어떤 타입`에 있어 `중요한 속성`이 있다면, 그 `하위 타입에서도` 그 속성은 `중요`함
					- 따라서, 타입에 작성된 `모든 메서드`는 *하위 타입에서도* `똑같이 잘 동작`해야 함
				- 하지만 위의 구현은 그렇지 못함
					- 만약 위 `equals`가 `Point` 클래스에서 사용됐다면, *`equals`를 사용해 동치관계를 비교하는 대부분의 작업*에서 `치환 원칙`이 `정상적으로 작용하지 않을 것`임
						- 예를 들어, *Point(1, 0) 인스턴스를 보관*하는 `Set`에 `ColorPoint(1, 0, Color.RED)`를 넣고 `contains` 를 호출하면 `false`를 반환 할 것인데, 이는 `Set과 같은 대부분의 컬렉션`에서 이러한 작업에 `equals` 연산을 사용하기 때문
							- x, y 값이 동일해도 ***타입이 다르므로*** `false`
##### 컴포지션 - 하위 클래스에 값을 추가하는 우회 방법
```java
public class ColorPoint {
	private final Point point;
	private final Color color;
	public ColorPoint(int x, int y, Color color) {
		point = new Point(x, y);
		this.color = Objects.requireNonNull(color);
	}
	/**
	* ColorPoint의 Point 뷰를 반환
	*/
	public Point asPoint() {
		return point;
	}
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof ColorPoint))
			return false;
		ColorPoint cp = (ColorPoint) o;
		return cp.point.equals(point) && cp.color.equals(color);
	}
}
```
- 위 케이스에서 `Point`를 상속하는 대신, `ColorPoint`에 `private Point 필드`를 두고, `ColorPoint`와 `동일한 위치(x, y)`의 Point를 반환하는 `view 메서드`를 추가하는 방식으로 우회 가능
#### 일관성
- 두 객체가 같다면 *둘 중 하나(또는 둘 다) 수정되지 않는 한* **`항상 동일하게 유지되어야 함`** 을 뜻함
- `가변 객체`의 경우 *비교 시점에 따라* `같거나 다를 수`도 있으나, `불변 객체`의 경우 한 번 같거나 다르면 그것이 **끝까지 유지되어야 함**
	- 즉, *클래스 작성시* `불변 클래스`로 만든다면 `equals`가 한번 `같다고 한 객체`와는 *항상 동등해야* 하며, `다르다고 한 객체`와는 *영원히 동등하지 않아야* 함
- 또한, 클래스의 *`가변`/`불변` 여부에 상관 없이* `equals`의 검사가 `신뢰할 수 없는 자원`에 **의존해서는 안됨**
	- 이를 위반할 경우, `일관성 조건`을 만족시키기 **매우 어려워짐**
	- Java 라이브러리의 [java.net.URL](https://docs.oracle.com/javase/8/docs/api/java/net/URL.html)이 이러한 경우임
		- `URL`의 `equals`는 주어진 `URL`과 `매핑된 호스트의 IP 주소`를 이용해 비교
		- 이때, `호스트 이름`을 `IP 주소`로 변환하려면 *네트워크를 통해야 하는데*, `변환 결과`가 `항상 같음을 보장할 수 없음`
		- 이 때문에 `URL`의 `equals`는 규약을 어기고 실무에서도 문제를 일으킴
	- 이를 피하려면, `equals`는 항상 `메모리에 존재하는 객체`만을 사용한 `결정론적 계산`(*예측할 수 있는 계산*)만을 수행해야 함
#### 널-아님(Non-nullity)
```java
// 1. 명시적인 null 검사 -> 필요하지 않음
@Override 
public boolean equals(Object o) {
	if (o == null)
		return false;
	// ...
}

// 2. 묵시적 null 검사
@Override
public boolean equals(Object o) {
	if (!(o instanceof MyType))
		return false;
	MyType mt = (MyType) o;
	// ...
}
```
- 이름 그대로, `모든 객체가 null과 같지 않아야 함`을 뜻함
	- `o.equals(null)`이 `true`를 반환하는 것은 물론, `NullPointerException`을 반환하는 상황도 **허용하지 않음** 
		- 즉, 그렇다고 위 코드의 1번 케이스처럼, `명시적인 null 검사`는 `필요하지 않음`
		- 차라리, 전달받은 객체를 `instanceof` 연산자를 통해 `올바른 타입인지`를 검사하는 것이 나음
			- `instanceof`의 경우 `첫 번째 피연산자(위 코드에서는 o)`가 null이라면 `두 번째 피연산자와 무관`하게 false를 반환
			- 또한, 내부에서 `동치성 검사`를 위해 *`형변환` 후 필드 값을 비교*할 때, `잘못된 타입`이 주어졌을 시 형변환에서 `ClassCastException`이 발생하는 것 역시 막아줌
## 정리 - 높은 품질의 `equals` 구현 과정
1. `==` 연산자를 사용해 `자기 자신에 대한 참조`인지 확인
	- 이를 통해 성능을 최적화 할 수 있음
2. `instanceof` 연산자로 `입력`이 `올바른 타입`인지 확인
	- 보통 올바른 타입은 `메서드(equals)`가 호출된 클래스임
	- 간혹, `이 클래스가 구현한 인터페이스`가 `"올바른 타입"`일 수 있음
		- 어떤 인터페이스는 자신을 구현한 (*서로 다른*) 클래스끼리 비교할 수 있도록 `equals` 규약을 수정하기도 함
			- 위 경우, `equals`에서 해당 인터페이스를 사용해야 하며, Java 라이브러리의 `Set`, `List`, `Map`, `Map.Entry` 등이 이에 해당
3. `입력`을 올바른 타입으로 `형변환` 함
	- `instanceof` 단계를 거쳤으므로, 이 단계는 실패하지 않음
4. 클래스의 `핵심(significant) 필드`들에 대해, `입력 객체`와 자신의 필드 값이 모두 일치하는지 검사함
	- 그 중 하나라도 다르면 `false`를 반환
	- 만약 `2단계(타입 검사)`에서 `인터페이스`를 사용한 경우, *입력의 필드 값을 가져올 때* `인터페이스의 메서드`를 사용해야 함
### 유의사항
#### 원시 타입 필드
- `==` 연산자를 사용하되, `float`과 `double`의 경우 각각 `Float.compare(float, float)` 또는 `Double.compare(double, double)`을 사용해 비교
	- 이는 `Float.NaN`, `-0.0f`와 같은 특수한 부동소수 값을 다루어야 하기 때문
	- `Float.equals`, `Double.equals`의 경우 오토박싱을 수반할 수 있어 성능이 떨어질 수 있음
####  참조 타입 필드
- 각각의 `equals` 메서드로 비교
- 배열의 경우, 각각의 원소들에 대해 앞선 지침대로 비교
	- 만약 배열의 `모든 원소`가 `핵심 필드`일 경우, `Array.equals`들 중 하나를 사용
#### null-가능 필드
- `null 값`을 `정상 값`으로 취급하는 필드의 경우, `Objects.equals(Object, Object)`로 비교해 `NPE`를 예방해야 함
#### 복잡한 필드
- 앞서 설명했던 `CaseInsensitiveString`과 같이 비교하기 복잡한 필드를 가진 경우
- 그 필드의 `표준형(canonical form)`을 저장해둔 뒤 표준형 끼리 비교하면 효율적임
	- 만약 *`가변 객체`일 경우*, 값이 바뀔 때마다 `표준형`을 `최신 상태로 갱신`해주어야 함
#### 필드 비교 순서
- 최상의 성능을 위해서는 `다를 가능성이 더 크거나`, `비교 비용이 저렴한 필드`를 먼저 비교할 것
	- `동기화용 lock 필드`처럼 객체의 논리적 상태와 상관없는 필드는 비교하지 말것
- `핵심 필드`와 *이를 통해 계산해낸* `파생 필드`가 있는 경우, `파생 필드를 비교하는 쪽`이 `더 빠를 수`도 있음
	- `파생 필드`가 `객체 전체의 상태를 대표`하는 상황이 대표적인 예
#### equals 구현 뒤 검토할 사항
- `대칭적`인가?, `추이성`이 있는가?, `일관적`인가?
	- 단순히 자문하는것에 그치지 말고, `단위 테스트`를 작성해 검사하는 것이 좋음
		- 만약 `equals`를 [AutoValue](https://github.com/google/auto/tree/main/value)를 이용해 작성한 경우 생략해도 됨
	- `반사성`과 `null-아님`도 만족해야 하지만, 이쪽이 문제가 되는 경우는 별로 없음
#### 이외 주의사항
- `equals` 재정의시, `hashCode`도 반드시 재정의할 것
- 너무 똑똑하게 코드를 작성하려고 하지 말 것
	- 필드들의 `동치성 검사`만해도 `equals` 규약은 지킬 수 있음
	- 오히려 너무 공격적으로 파고들면 문제가 발생할 수 있음
		- 예를 들어, `File` 클래스 간 `심볼릭 링크(다른 파일에 대한 참조, 별칭)`까지 비교해 동치성을 검사한다던지 등의 행동을 말함
- `Object`외의 타입을 매개변수로 받는 `equals`는 선언하지 말 것
	```java
	public boolean equals(MyClass o) {
		// ...
	}
	```
	- 위 코드와 같은 경우를 말함
	- 이는 `Object.equals`를 재정의한 것이 아닌, `다중정의(오버로딩)`한 것
		- 이는 하위 클래스의 `@Override` 애너테이션이 `긍정 오류(false positive)`를 내게 하고 `보안 측면`에서도 `잘못된 정보를 제공`함
	- 이번 아이템에서 나온 예제 코드들처럼, `@Override` 애너테이션을 `일관되게 사용`할 경우 이러한 실수는 `예방`할 수 있음
#### AutoValue
- `equals` 및 `hashCode`를 작성 및 테스트하는 작업을 대신해주는 Google의 오픈소스 프레임워크
- 클래스에 애너테이션 하나(`@AutoValue`)만 추가하면 이 메서드들을 알아서 작성해줌
- IDE 측에서 이러한 기능을 제공하지만, 생성된 코드의 깔끔함은 AutoValue쪽이 더 좋음
	- 또한, IDE는 `클래스가 수정된 것`을 *자동 감지하지 못하므로*, 테스트 코드를 작성해줘야 함
# 정리
- 꼭 필요한게 아니면, `equals`를 재정의하지 말 것
- 대부분의 경우 `Object`의 `equals`가 우리가 원하는 비교를 정확히 수행해줌
- *만약 재정의한다면*, 클래스의 `핵심 필드`들을 모두, `다섯 가지 규약`을 `확실히 지키며 비교`하도록 작성해야함