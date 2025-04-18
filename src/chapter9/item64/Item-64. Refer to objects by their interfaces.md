객체는 인터페이스를 사용해 참조하라
## 가능한 반환값, 변수, 필드를 인터페이스 타입으로 선언하라
```java
// 좋은 예: 인터페이스를 타입으로 사용함
Set<Son> sonSet = new LinkedHashSet<>();

// 나쁜 예: 클래스를 타입으로 사용함
LinkedHashSet<Son> sonSet = new LinkedHashSet<>();
```
- 실제 클래스는 오직 `생성자를 통해 생성`할 때만 사용할 것
- 이처럼 `인터페이스`를 타입으로 `사용`하는 습관을 들이면 `프로그램의 유연성`을 높일 수 있음
	```java
		// Set<Son> sonSet = new LinkedHashSet<>();
		Set<Son> sonSet = new TreeSet<>();
		
		// 위처럼 구현 클래스를 변경해도, 이와 관련된 주변 코드들은 변화에 영향을 받지 않음
		sonSet.add(new Son(...));
		...
	```
	- *구현 클래스를 교체할 때*, `표현식의 오른쪽 값`만 바꿔주면 되기 때문
	- 또한, 관련된 `주변 코드`들도 변화의 영향을 받지 않음
### 예외 - 주변 코드가 클래스의 특정 기능에 의존하는 경우
- 인터페이스에 존재하지 않고, *`클래스에만 존재하는 기능`* 에 `주변 코드가 의존`할 경우 새로운 클래스도 `해당 기능을 제공`해야 함
	- 예를 들어, `TreeSet`의 오름차순 정렬 정책을 가정하고 주변 코드를 작성한 경우, 이들을 `HashSet`으로 바꿀 시 문제가 발생할 수 있음
### 적합한 인터페이스가 없다면 클래스로 참조하라
- *적합한 인터페이스가 없다면* 클래스를 참조하되, `클래스 계층구조` 중 *필요한 기능을 만족하는* `가장 추상적인(계층구조에서 상위인) 클래스`를 사용해야 함
- 클래스를 참조해야 하는 경우는 크게 다음 3가지 경우가 있음
#### 값 클래스
- `String`, `BIgInteger`와 같은 값 클래스는 상응하는 인터페이스가 존재하는 경우가 드물기때문에, 그대로 매개변수/변수/필드/반환 타입으로 사용함
#### 클래스 기반으로 작성된 프레임워크에 속한 객체
- 이 경우, *구현 클래스보다* `추상적인 기본 클래스`를 참조하는 것이 바람직함
	- `java.io` 패키지의 `OutputStream`과 같은 클래스가 `기본 클래스`의 대표적인 예시
#### 인터페이스에 없는 특별한 메서드를 제공하는 클래스
- `Queue` 인터페이스에 존재하지 않는 `comparator` 메서드를 제공하는 `PriorityQueue` 클래스와 같은 경우가 대표적인 예시