비트 필드 대신 EnumSet을 사용하라.
## 열거 값이 집합으로 사용되는 경우
### enum 등장 이전 - `비트 필드(bit field)`
이전에는 각 상수에 2의 거듭제곱 값을 할당한 `정수 열거 패턴`을 사용해왔다.
```java
public class Text {
	public static final int STYLE_BOLD = 1 << 0; // 1
	public static final int STYLE_ITALIC = 1 << 1; // 2
	public static final int STYLE_UNDERLINE = 1 << 2; // 3
	public static final int STYLE_STRIKEROUGH = 1 << 3; // 8
	// styles << 0개 이상의 STYLE_... 값들을 OR 연산한 값이다.
	public void applyStyle(int styles) { ... }
}
```
위의 예의 `styles` 와 같이 OR 연산을 통해 여러 상수를 한 집합으로 모은 것을 **`비트 필드(bit field)`** 라고 한다.
### 비트 필드의 장-단점
> [!tip] 장점
> 1. 비트 연산을 활용해 합집합, 교집합과 같은 **집합 연산**을 **효율적으로 수행**할 수 있다.

>[!warning] 단점
> 1. **정수 열거 상수의 단점**을 그대로 지니고 있다.
> 2. 비트 필드값을 **출력**했을 때, **해석이 힘들다**.
> 3. 비트 필드에 **포함되어 있는 값**을 **순회하기 힘들다**.
> 4. API를 작성할 때 **최대 몇 비트가 필요할지 예상**해야 한다.
## 비트 필드의 대안 - `EnumSet`
`java.util.EnumSet`은 enum 타입 상수 값으로 구성된 집합의 효과적 표현을 돕는다.
EnumSet은 ① 타입 안전성을 지녔고, ② Set 인터페이스를 구현하고 있으며, ③ 다른 Set 구현체들과 호환된다.
내부적으로는 bit vector로 구현되어 있어, 원소가 64개 이하라면 EnumSet 전체를 long 변수 하나로 표현할수 있어 비트 필드와 비슷한 성능을 보인다.
- bit vector: 
대량의 작업, 이를테면 `removeAll`, `retainAll`과 같은 작업은 비트단위 산술 연산을 통해 구현되어 있다.
그러나 EnumSet이 힘든 작업들을 대신해주어 수동으로 비트를 조작할때 발생할 수 있는 오류들로 부터 자유로울 수 있다.
`EnumSet`은 `of`와 같은 메서드처럼 *집합을 생성한다거나* 하는 기능을 가진 **정적 팩토리 메서드**들을 **제공**한다.

# 핵심
- 열거할 수 있는 타입을 모아 집합 형태로 쓴대도 **비트 필드를 쓸 이유는 없다**.
	- `EnumSet` 클래스가 비트 필드 만큼의 명료함, 성능을 제공할 뿐만 아니라 enum 타입의 장점도 함께 가져갈 수 있기 때문이다.
- 다만, (Java 9까진) **불변** `EnumSet`을 만들 수 없다는 단점이 존재한다.
	- 그 전까지는 `Collections.unmodifiableSet`으로 EnumSet을 감싸거나, 구글의 `Guava` 라이브러리의 [`ImmutableEnumSet`](https://github.com/google/guava/blob/master/guava/src/com/google/common/collect/ImmutableEnumSet.java) 을 이용해야 한다.