명명 패턴보다 어노테이션을 사용해라.
# 명명 패턴(Naming Pattern)
전통적으로 Tool, Framework가 다뤄야 할 프로그램 요소에는 구분되는 명명 패턴을 사용했다.
## 명명 패턴의 단점
### 오타가 나서는 안 된다.
일례로 JUnit은 버전 3 까지 테스트 메서드 이름을 test로 시작하게 했다.
따라서 test라고 써야할 곳에 `tset`라고 오타를 내면 JUnit은 해당 메서드를 무시하게 된다.
### 적절한 프로그램 요소에만 적용되고 있다는 것을 보증할 방법이 없다.
클래스에 정의된 모든 테스트 메서드를 실행해달라는 의도로 클래스 이름을 `TestSafetyMechanisms`라고 지어 JUnit에게 넘겨도, JUnit은 클래스 이름에 관심 없어 테스트를 수행하지 않는다.
### 프로그램 요소를 매개변수로 전달할 방법이 없다.
특정 테스트에서 기대하는 예외의 타입을 매개변수로 넘겨야 할때, 이를 메서드 이름에 덧붙이는 방법을 생각 할 수 있지만 이는 깨지기 쉽다.
자바 컴파일러는 메서드 이름에 추가된 문자열이 무엇을 의미하는지, 어떤 이름의 클래스가 존재하는지 알 방법이 없다.
# 명명 패턴의 대체재 - `애너테이션(Annotation)`
테스트 프레임워크를 통해 애너테이션을 사용하는 예제를 만들고자 한다.
## 매개변수를 받지않는 애너테이션
Test라는 '예외 발생시 해당 테스트를 실패 처리'하는 간단한 애너테이션을 만들어 예시를 들고자 한다.
```java
// @Test가 런타임에도 유지되어야 한다는 표시이다.
// 이를 생략하면, 테스트 도구는 @Test를 인식할 수 없다.
@Retention(RetentionPolicy.RUNTIME)
// @Test가 메서드 선언에서만 사용되어야 한다는 표시이다.
// 따라서, @Test는 클래스 선언, 필드 선언 등의 요소에는 달 수 없다.
@Target(ElementType.METHOD)  
public @interface Test {  
}
```
해당 애너테이션 자체도 두 가지의 다른 애너테이션이 달려있다.
### `메타-애너테이션(meta-annotation)`
위의 `@Retention`, `@Target` 처럼 애너테이션에 다는 애너테이션을 메타-애너테이션이라고 한다.
### `마커 애너테이션(marker annotation)`
`@Test` 처럼 아무 매개변수 없이 대상을 "마킹"하는 애너테이션을 말한다.
이러한 애너테이션을 사용하므로써, *애너테이션이 달린 코드에는 영향 없이* 해당 애너테이션에 **관심이 있는 프로그램**에게 **정보를 제공** 할 수 있다.
예시의 `RunTests`와 같은 테스트 러너가 그러한 프로그램의 예시다.
### 자바 컴파일러에게 애너테이션 구현 사항을 강제하기
위의 애너테이션에 `"매개변수가 없는 정적 메서드" 에서만 쓸 수 있다.` 라는 주석이 달려있는데, 이를 **컴파일러에게 강제**하려면 **애너테이션 처리기**를 직접 **구현**해야 한다.
만약 처리기 없이 *인스턴스 메서드*, 또는 *매개변수가 있는 메서드*에 `@Test` 애너테이션을 단다면 *컴파일은 되지만* **테스트 도구**를 실행할 때 **문제가 발생**한다.
- 처리기 구현은 `javax.annotation.processing` API 문서 참고
## 매개변수를 받는 애너테이션
특정 예외를 던져야 성공하는 애터네이션을 만들어보자.
```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface ExceptionTest {  
    // 이 애너테이션의 매개변수 타입은 `Class<? extends Throwable>` 이다.  
    // Throwable을 extend한 클래스 객체, 즉 모든 Exception과 Error 타입을 수용한다.  
    Class<? extends Throwable> value();  
}
```
해당 애너테이션을 사용하려면, Exception 또는 Error 타입의 class 리터럴을 인자로 넘겨야 한다.
만약 테스트 러너가 컴파일을 잘 수행했다면 매개변수로 넘어간 Exception이 올바른 타입이라는 것을 의미한다.
만약, 해당 예외의 클래스 파일이 런타임에 사라진다면 `TypeNotPresentException`을 던진다.
이후 매개변수로 전달되었던 클래스는
```java
// m은 메서드 이다.
Class<? extends Throwable> excType =  
        m.getAnnotation(ExceptionTest.class).value();
```
와 같은 방식으로 꺼낼 수 있다.
## 배열로 구성된 매개변수를 받는 애너테이션
여러 개의 예외 중 하나가 발생하면 성공하는 테스트가 있다고 하자.
이럴 경우, 애너테이션 매개변수의 타입을 배열로 변경하면 된다.
```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface ExceptionTests {  
    Class<? extends Exception>[] value();  
}
```
배열 매개변수를 받는 문법은 `단일 원소 배열(single-element array)`에 최적화 되어있지만, 이전의 `ExceptionTest`들도 수용 가능하다.
### 배열 매개변수를 넘기는 방법
```java
// 원소들을 중괄호로 감싼 뒤, 쉼표를 통해 구분해준다.
@ExceptionTest({ IndexOutOfBoundsException.class,
NullPointerException.class })
public static void doublyBad() {
	List<String> list = new ArrayList<>();
	// The spec permits this method to throw either
	// IndexOutOfBoundsException or NullPointerException
	list.addAll(5, null);
}
```
위와 같이 원소로 넘길 클래스들을 중괄호로 감싼 뒤, 쉼표를 통해 구분해준다.
## 여러 개의 값을 받는 또 다른 방식 - `@Repeatable`
Java 8에서는 [`@Repeatable`](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Repeatable.html) 애너테이션을 이용해 여러 개의 값을 받는 애너테이션을 만들 수 있다.
배열 매개변수 대신, 애너테이션에 `@Repeatable` 메타-애너테이션을 달면 된다.
`@Repeatable`이 달려있는 애너테이션은 하나의 요소에 여러 번 적용할 수 있다.
### `@Repeatable`을 쓸 때 주의점
1. *`@Repeatable` 이 달린 애너테이션을 반환*하는 '**컨테이너 애너테이션**'을 하나 더 정의한 뒤, `@Repeatable`에 **'컨테이너 애너테이션'의 class**를 매개변수로 넘겨야 한다.
2. '컨테이너 애너테이션'은 *내부 애너테이션 타입의 배열*을 **반환**하는 `value` 메서드를 정의해야 한다.
3. '컨테이너 애너테이션'  타입에는 적절한 보존 정책`(@Retention`), 적용 대상(`@Target`)을 명시해주어야 **컴파일이 정상적으로 진행**된다.
즉, 다음과 같이 해야한다.
```java
// 반복 가능한 애너테이션
// '컨테이너 애너테이션'의 배열의 원소로 들어갈 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
	Class<? extends Exception> value();
}
// 컨테이너 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
	ExceptionTest[] value();
}
```
이를 사용한 예시는 다음과 같다.
```java
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doublyBad() { ... }
```
### `@Repeatable`을 처리할 때 주의점
`@Repeatable`을 여러 개 달면, 하나를 달았을 때와 구분하기 위해 '컨테이너 애너테이션' 타입이 적용된다.
`getAnnotationsByType` 메서드는 둘을 구분하지 않아 `@Repeatable이 달린 애너테이션`과 그의 컨테이너 애너테이션을 모두 가져온다.
반면 `isAnnotationPresent` 메서드는 이 둘을 명확히 구분해, `isAnnotationPresent`에 '*`@Repeatable` 애너테이션을 여러 번 단 것*'을 검사한다면 '컨테이너 애너테이션' 을 단 것으로 인식해  `@Repeatable이 달린 애너테이션`인지를 검사하면 *그렇지 않은것으로 인식*하고 **무시**해버린다.
반대로 *'컨테이너 애너테이션'인지를 검사*한다면, `@Repeatable이 달린 애너테이션`이 **하나 달린 메서드**는 **무시**하게 된다.
따라서, 두 경우 모두 검사하려면 두 케이스를 따로 따로 확인해야 한다.
예제에서는 아래와 같이 or을 통해 검사했다.
```java
if (m.isAnnotationPresent(ExceptionWithRepeatable.class) || m.isAnnotationPresent(ExceptionWithRepeatableContainer.class))
```
## `@Repeatable`의 특징
`@Repeatable`을 사용하면, 한 요소에 같은 애너테이션을 여러 번 달 때 **가독성을 높힐 수** 있다.
하지만, 애너테이션 *선언 및 처리 과정*에서 **코드가 늘어나고**, 처리 **코드가 복잡해져 오류가 날 가능성**이 커질 수 있다.
# 핵심
- *애너테이션이 명명 패턴보다 나으므로*, 애너테이션으로 할 수 있는 일을 굳이 명명 패턴으로 처리할 이유는 없다.
- *툴을 제작하는 개발자가 아니라면*, 애너테이션 타입을 **직접 정의할 일은 거의 없다**.
	- 하지만, *자바 개발자라면* 예외 없이 **자바가 제공하는 애너테이션 타입들은 사용해야 한다**.
		- IDE, `정적 분석 도구(static analysis tools)`가 제공하는 애너테이션을 사용한다면 해당 tool들이 제공하는 진단 정보의 품질을 높일 수 있다.
			- 다만 해당 애너테이션들은 표준이 아니므로, tool을 바꾸거나 표준이 만들어진다면 코드의 수정이 필요할 것이다.
- `정적 분석 도구` : 코드를 실행하지 않고 검사할 수 있는 도구. JUnit, SonarQube, Checkstyle, Spotbugs, PMD Java 등의 도구가 있다.