# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

- 대부분의 클래스는 여러 리소스에 의존한다.
    - SpellChecker가 Dictionary를 사용하고, 이를 의존하는 리소스 또는 의존성이라고 부른다.

> - static 유틸리티를 잘못 사용한 예
```java
public class SpellChecker {
	private static final Lexicon dictionary = ...; // 의존하는 리소스 (의존성)
	private SpellChecker() {} // 객체 생성 방지 

	public static boolean isValid(String word) { ... } 
	public static List<String> suggestions(String typo) { ... } 
}
```

> - 싱글턴을 잘못 사용한 예
```java
public class SpellChecker {
	private final Lexicon dictionary = ...;
	private SpellChecker() {}
    public static SpellChecker INSTANCE = new SpellChecker();

	public boolean isValid(String word) { ... } 
	public List<String> suggestions(String typo) { ... } 
}
```

- 두 방식 모두 사전을 단 하나만 사용한다고 가정했기 때문에, 좋지 않은 방식이다.
- 직접 명시되어 고정되어 있는 변수는 테스트를 하기 힘들게 만든다.
- 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.

- ## 의존성을 바깥으로 분리하여 외부로부터 주입받도록 해야 한다.

```java
public class SpellChecker { 
	private final Lexicon dictionary; 

	private SpellChecker(Lexicon dictionary) { 
    		this.dictionary = Objects.requireNonNull(dictionary); 
 	} 

	public boolean isValid(String word) { return true; } 
	public List<String> suggestions(String typo) { return null; } 
}
```
- 불변을 보장하여 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다.


## 쓸만한 변형 : 생성자에 자원 팩터리를 넘겨주기
- 팩터리 : 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체

ex) `Supplier<T>` 인터페이스
```java
@FunctionalInterface
public interface Supplier<T> {
		/**
     * Gets a result.
     *
     * @return a result
     */
    T get(); // T 타입 객체를 찍어낸다
}
```

```java
Mosaic create(Supplier<? extends Tile> tileFactory) { ... }
```
- `Supplier<T>`를 입력으로 받는 메서드는 한정적 와일드카드 타입을 사용해 팩터리의 타입 매개변수를 제한한다.
  - 클라이언트는 자신이 명시한 타입의 하위 타입이라면 무엇이든 생성할 수 있는 팩터리를 넘길 수 있다.

## 핵심 정리
- 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다.
- 이 자원들을 클래스가 직접 만들게 하지 말고, 자원을 만들어주는 팩터리를 생성자에 넘겨주자.
  - 의존 객체 주입을 하자