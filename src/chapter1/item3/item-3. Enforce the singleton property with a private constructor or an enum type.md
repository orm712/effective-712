`private 생성자` 또는 `열거(enum) 타입`으로 싱글턴임을 보증하라
# 싱글턴
- 인스턴스를 `오직 하나`만 `생성`할 수 있는 클래스
- 전형적인 예
	- `무상태(stateless) 객체`
		- ex) 함수
	- `설계상 유일`해야 하는 `시스템 컴포넌트`
## 싱글턴의 문제점
- `클래스`를 `싱글턴`으로 만들 경우, 이를 사용하는 `클라이언트의 테스트`가 **`어려워질 수 있음`**
	- 싱글턴 인스턴스를 `모의(mock) 구현체`로 `대체할 수 없기 때문`
		- 단, 타입을 인터페이스로 정의한 뒤, 그 `인터페이스를 구현하여 만든 싱글턴`은 제외
## 싱글턴을 만드는 방식
- 싱글턴을 만드는 방식은 보통 다음 두 가지 방식 중 하나임
	 1. `public static final` 필드를 사용하는 방법
	 2. `정적 팩터리`를 사용하는 방법
 - 두 방식은 다음과 같은 `공통점`을 갖고 있음
	 - `생성자`를 `private`으로 감춰둠
	 - 유일한 인스턴스에 접근할 수 있는 `public static 멤버`를 둠
### `public static final` 필드 방식
```java
public class Elvis {
	public static final Elvis INSTANCE = new Elvis();
	private Elvis() { ... }
	public void leaveTheBuilding() { ... }
}
```
- `public static final` 필드로 싱글턴 인스턴스를 외부에 제공하는 방식
- `private 생성자`는 `public static final`필드가 `초기화`될 때 `단 한 번`만 `호출`됨
- 또한, *외부에서 접근 가능한 생성자가 없으므로* 해당 클래스는 전체 시스템에서 `단 하나의 인스턴스`(*클래스가 초기화될 때 만들어진*)만 `존재함이 보장`됨
	- 단, 한 가지 예외가 존재
		- `Reflection API`중 하나인 `AccessibleObject.setAccessible`을 이용할 경우 `private 생성자`를 호출할 수 있게 됨
			- `AccessibleObject.setAccessible(boolean flag)`이란, [`AccessibleObject`](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/AccessibleObject.html#setAccessible-java.lang.reflect.AccessibleObject:A-boolean-)(필드, 메서드, 생성자 객체의 베이스 클래스)의 메서드로 *`Reflect된 객체가 사용될 때`* Java 언어단의 **`접근 제어 검사`** 를 `억제 할 지 말 지`에 대한 `flag를 설정`하는 메서드
				- 즉, `flag`가 `true`로 설정된 경우, `Reflect된 객체`가 사용될 때 `Java 언어단의 접근 제어 검사`를 **`억제`** 하며, `false`인 경우 `접근 제어 검사`를 **`강제`** 함
		- 예시 코드
		```java
		// testcodes/Elvis.java
		package testcodes;

		public class Elvis {
			public static final Elvis INSTANCE = new Elvis();
			// Elvis 클래스는 생성자로 private으로 설정된 생성자 단 하나만 가짐
			private Elvis() { }
			public void leaveTheBuilding() { }
		}

		// testcodes/ElvisMain.java
		package testcodes;  
		  
		import java.lang.reflect.*;  
		  
		public class ElvisMain {  
			public static void main(String[] args) throws Exception {  
				// getDeclaredConstructor를 통해 Elvis 클래스에 선언된 생성자를 가져옴(getDeclaredConstructor는 생성자의 접근 제어자에 관계없이 생성자를 가져올 수 있음)
				// 단, private으로 설정된 생성자이므로 아직 해당 생성자를 사용할 순 없음
				Constructor elvisConstructor = Class.forName("testcodes.Elvis").getDeclaredConstructor();  
				// Constructor는 AccessibleObject의 자식으로, setAccessible 메서드 보유. 해당 메서드를 사용해 접근 제어 검사를 억제
				elvisConstructor.setAccessible(true);  
				// private 생성자를 사용해 새로운 인스턴스 생성
				Elvis elvis = (Elvis) elvisConstructor.newInstance();  
			}  
		}
			```
		- 이를 방지하기 위해서는 생성자를 수정하여, 객체가 두 번 이상 생성되려 하면 예외를 던지도록 코드를 변경해야 함
#### 장점
- 해당 클래스가 싱글턴임이 코드 자체에서 명확하게 드러남
	- `public static`이면서, `final`이기 때문에 다른 객체를 참조하는 것이 불가능
- 코드의 간결함
### `정적 팩터리` 방식
```java
public class Elvis {
	private static final Elvis INSTANCE = new Elvis();
	private Elvis() { ... }
	// 인스턴스 멤버를 제공하는 정적 팩터리 메서드
	public static Elvis getInstance() { return INSTANCE; }
	public void leaveTheBuilding() { ... }
}
```
- `public static final` 메서드를 통해 `private static final`로 선언된 `싱글턴 인스턴스`를 외부에 제공하는 방식
- 마찬가지로 `private 생성자`는 `private static final`필드가 `초기화`될 때 `단 한 번`만 `호출`됨
#### 장점
- 코드의 유연함
	- API를 바꾸지 않고도 `싱글턴이 아닌 방식`으로 `변경`할 수 있음
		- `정적 팩터리 메서드` 내용만 `변경`해주면, 싱글턴이던 기존의 코드를 스레드별로 다른 인스턴스를 제공해준다거나 하는 식으로 변경할 수 있게됨
	- (원할경우) `제네릭 싱글턴 팩터리`로 변경할 수 있음
- 정적 팩터리 `메서드의 참조`를 `공급자(Supplier)`로 사용할 수 있음
	- ex) `Elvis::getInstance`를 Supplier\<Elvis\>로 사용하는 식
### 직렬화
- 싱글턴 클래스를 직렬화하기 위해서는 `Serializable` 구현 이상의 작업이 필요
	1. 모든 인스턴스 필드를 `transient`(일시적) 키워드로 선언한다.
		- `transient`: 해당 키워드가 선언된 필드는 직렬화를 수행하지 않음
			- `transient`로 선언된 필드는 역직렬화시 해당 타입의 `디폴트 값`(`0`, `null` 등)이 자동으로 할당됨
	2.   `readResolve` 메서드를 제공해야 한다. [참고 - Java Object Serialization Specification - The readResolve Method](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/input.html#a5903)
		- `readResolve`란, 역직렬화를 수행하는 `ObjectInputStream`이 *스트림에서 읽어낸 객체가 아닌* `다른 값으로 대체`할 수 있도록 해주는 메서드
			- `ObjectInputStream`는 역직렬화할 객체의 클래스가 `readResolve` 메서드를 정의하고 있는지 확인하고, 만약 정의하고 있다면 `readResolve`가 호출되고 반환 값을 역직렬화의 결과값으로 대체됨
		- 이렇게 하지 않으면, 직렬화된 인스턴스를 역직렬화 할 때 마다 새로운 인스턴스가 생성됨
### 싱글턴을 만드는 또 다른 방식, 열거 타입
```java
public enum Elvis {
	INSTANCE;

	public void leaveTheBuilding() { ... }
}
```
- 이전의 `public static` 필드와 유사하지만, 다음과 같은 장점을 갖고 있음
	- 더 간결하게 코드를 작성할 수 있음
	- 직렬화를 위한 추가적인 코드 수정이 필요하지 않음
	- 앞서 소개된 `Reflection` 공격이나 새로운 인스턴스가 생기는 일을 방지함
- 조금 부자연스러워보이기는 하나, 대부분의 경우 원소가 하나인 열거 타입을 사용하는 것이 가장 좋은 싱글턴 구현 방법임
	- 단, 싱글턴이 `Enum 외의 클래스`를 `상속`해야하는 경우, 사용 불가능한 방법
		- 다른 인터페이스를 구현하여야 하는 경우는 괜찮음