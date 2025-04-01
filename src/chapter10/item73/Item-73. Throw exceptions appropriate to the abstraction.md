추상화 수준에 맞는 예외를 던지라
## 저수준 예외를 전파하지 말 것
- 이는 수행하려는 일과 `관련 없어보이는 예외가 등장`해 개발자를 당황시키고, *내부 구현 방식을 드러내* `상위 레벨의 API를 오염`시킴
	- 즉, 클라이언트가 *해당 예외를 처리하는 코드를 작성할 경우*, 다음 릴리스에서 `다른 예외를 던지게 되면` 클라이언트 프로그램이 `깨질 수 있음`
### 해결 방법 1) 예외 번역
```java
try {
	... // 저수준 추상화를 이용해 코드 수행
	} catch (LowerLevelException e) {
	// 잡은 저수준 예외를 추상화 수준에 맞는 예외로 바꾸어 던짐
	throw new HigherLevelException(...);
}
```
- 즉, `상위 계층`에서 *저수준 예외를 잡아* `현재 추상화 수준에 맞는 예외`로 바꿔 던지는 `예외 번역(Exception Translaiton)`이 필요
	- 주의
		- `저수준 예외를 고수준 예외로 바꾸어 던진다`라는게 두 예외 간 `상위-하위 관계`가 있어야 한다는 것이 아님
		- 아래 예시처럼, 꼭 *`상위-하위 관계`에 놓이지 않더라도* `사용자가 더 잘 이해할 수 있는 예외`로 바꾸는 것도 `예외 번역`이라고 부름
##### 예시 - AbstractSequentialList
```java
/**
* 리스트 안의 지정된 위치의 원소를 반환한다.
* @throws IndexOutOfBoundsException index가 범위를 벗어난 경우
* ({@code index < 0 || index >= size()}).
*/
public E get(int index) {
	ListIterator<E> i = listIterator(index);
	try {
		return i.next();
	} catch (NoSuchElementException e) {
	throw new IndexOutOfBoundsException("Index: " + index);
	}
}
```
### 해결 방법 2) 예외 연쇄
```java
try {
	... // 저수준 추상화를 이용해 코드 수행
} catch (LowerLevelException cause) {
	throw new HigherLevelException(cause);
}
```
- `예외 번역`의 특별한 형태로, 문제의 `근본 원인`(`저수준 예외`)이 *디버깅 시 도움이 된다면* `고수준 예외에 함께 실어` 던지는 방식을 의미
	- 실어진 `저수준 예외`는 `Throwable`의 `getCause` 메서드를 통해 접근할 수 있음
```java
class HigherLevelException extends Exception {
	// 예외 연쇄용 생성자
	HigherLevelException(Throwable cause) {
		super(cause);
	}
}
```
- `고수준 예외`의 `생성자`는 상위 클래스 생성자에 `저수준 예외(cause)`를 건네어, 최종적으로 `Throwable(Throwable)` 생성자 까지 올라감
	- 대부분의 표준 예외는 `예외 연쇄용 생성자`를 갖추고 있음
		- 만약 그렇지 않은 예외라도, `Throwable` 클래스의 `initCause` 메서드를 사용하면 `근본 원인`이 무엇인지 명시하는게 가능함
### 주의사항
- `예외 번역`이 무책임한 예외 전파보다 낫다고 `남용해서는 안 됨`
	- 가능한 저수준 계층의 메서드가 성공하도록 해 `저수준 예외가 발생하지 않도록 하는게 좋음`
		- 예를 들어 상위 계층 메서드에서 *하위 계층 메서드로* `매개변수를 건네기 전 검사`를 하는 작업을 수행하는 등의 작업을 하라는 의미
	- 만약 *하위 계층의 예외를 피할 수 없다면*, `상위 계층에서 조용히 처리`하되 `java.util.logging`과 같은 기능을 활용해 `기록을 남길 것`
# 핵심 정리
- 하위 계층의 예외를 `예방 및 자체 처리하는게 불가능`하고, *상위 계층에 그대로 노출하기 힘들 경우* `예외 번역`을 사용할 것
- `예외 연쇄`를 활용하면 상위 계층에 `맥락에 어울리는 고수준 예외`를 던짐과 함께 `근본 원인`도 제공할 수 있어, `오류 분석이 쉬워짐`