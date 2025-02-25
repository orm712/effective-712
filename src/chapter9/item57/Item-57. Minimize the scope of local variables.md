지역변수의 범위를 최소화하라
## 지역 변수의 범위를 줄이는 방법
- `지역 변수`의 `유효 범위`를 줄이면 `코드 가독성`과 `유지보수성`은 높아지고, `오류 가능성`은 낮아짐
### 처음 사용될 때 선언하기
- 미리 선언해놓을 경우 `코드 가독성이 떨어지고`, 변수를 사용할 시점에 `타입 및 초깃값`이 기억나지 않을 수도 있음
- 또한, *실제 `사용하는 블록 바깥`에 선언할 경우*, 블록이 끝난 뒤에도 변수가 살아 있으므로 `의도한 범위 앞/뒤`에서 해당 `변수 사용시 오작동` 할 수 있음
### 선언과 동시에 초기화 하기
- *`초기화에 필요한 정보`가 충분하지 않다면*, **선언을 미룰 것**
- `try-catch`문의 경우, 경우가 달라짐
	- `변수 초기화 표현식`이 예외를 던질 가능성이 있다면 `try` 블록 안에서 초기화해야 함
	- 또한, 변수 값을 `try 블록 바깥에서 사용`해야 한다면, `try 블록 앞`에서 선언해야 함(정확히 초기화는 못한다 해도)
#### 반복문의 변수 범위
```java
// 권장되는 컬렉션 또는 배열 순회 구문
for (Element e : c) {
	... // Do Something with e
}

// 반복자(Iterator)를 써야 할 경우, 전통적인 for 문을 사용하는게 나음
for (Iterator<Element> i = c.iterator(); i.hasNext(); ) {
	Element e = i.next();
	... // Do something with e and i
}
```
- `for`, `for-each`와 같은 반복문에서는 `반복 변수(loop variable)`의 범위가 `반복문의 몸체`, *for 키워드와 몸체 사이의* `괄호 안`으로 제한
- 반복 변수의 값을 반복문 이후에 사용할 게 아니라면, *`while 문` 보단* `for 문`이 나음
	```java
	// while 문을 사용한 구현 - 버그 발생
	Iterator<Element> i = c.iterator();
	while (i.hasNext()) {
		doSomething(i.next());
	} ...
	Iterator<Element> i2 = c2.iterator();
	while (i.hasNext()) { // BUG!
		// i는 이미 c의 끝에 도달했으므로, while 문의 조건에 막혀 c2는 순회하지도 않고 끝나버림
		doSomethingElse(i2.next());
	}

	// for문을 사용한 구현 - 컴파일 오류 발생
	for (Iterator<Element> i = c.iterator(); i.hasNext(); ) {
		Element e = i.next();
		... // Do something with e and i
	} ...
	// 컴파일 시간 에러 - i를 찾을 수 없음
	for (Iterator<Element> i2 = c2.iterator(); i.hasNext(); ) {
		Element e2 = i2.next();
		... // Do something with e2 and i2
	}
	```
	- `while 문`을 사용할 경우, 위처럼 `복사-붙여넣기` 때문에 **이전의 변수를 또 사용**하는 코드가 `컴파일 및 동작`할 수 있음
	- 또한, `while 문` 보다 `for 문`이 **더 짧아** `가독성이 좋음`
### 메서드를 작게 유지하고, 하나의 기능에 집중할 것
- 한 메서드에서 `여러 기능들을 처리`한다면, 그 중 `하나의 기능과 관련`된 `지역변수`가 *메서드 내에서 `다른 기능을 수행하는 코드`* 에서 접근할 수 있음
- 따라서, *기능별로* `메서드를 쪼개는 것`을 권장