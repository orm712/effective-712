인터페이스는 타입을 정의하는 용도로만 사용하라.
# 인터페이스의 역할
인터페이스는 '자신을 구현한 클래스의 인스턴스'를 **참조**할 수 있는 **타입 역할**을 한다.
## 반례 - 상수 인터페이스
상수 인터페이스는 메서드 없이 `static final` 필드로만 이뤄진 인터페이스로, 명백한 **안티 패턴**이다.
이러한 상수들을 쓰고자 하는 클래스에서 해당 인터페이스를 implements하곤 한다.
```java
public interface PhysicalConstants {
	// Avogadro's number (1/mol)
	static final double AVOGADROS_NUMBER = 6.022_140_857e23;
	// Boltzmann constant (J/K)
	static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
	// Mass of the electron (kg)
	static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```
### 문제점
클래스 내부에서 사용하는 상수는 내부 구현사항(코드, 설계)에 해당되므로, 상수 인터페이스를 implements 하는 것은 내부 구현사항을 클래스 API로 노출하는 행위인 것이다.
(해당 클래스에서 어떤 상수 쓴다는 걸 외부에 밝히는 꼴)
이러한 행위는 사용자에게 혼란을 유발하거나, 클라이언트의 코드가 해당 상수들에 종속되게 만든다.
따라서, 클래스에서 *더는 해당 상수들을 사용하지 않아도* [**바이너리 호환성**](https://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html)을 위해 **여전히 상수 인터페이스를 implements** 하고 있어야 한다.
게다가 *`final`이 아닌*, 즉 상속의 가능성을 열어둔 클래스가 상수 인터페이스를 implements 한다면 **모든 하위 클래스들의 namespace**가 **인터페이스의 상수들**로 **오염**된다.
Java Platform Library에도 `java.io.ObjectStreamConstants`와 같은 상수 인터페이스가 존재한다.
## 대안 - 상수를 공개할 수 있는 선택지
### 특정 클래스/인터페이스에 연관된 경우
해당 클래스/인터페이스 자체에 추가한다.
이러한 방식을 쓴 대표적인 예시로는 모든 실수/정수 타입의 박싱 클래스가 있다.
`Integer`에 선언된 `MAX_VALUE`, `MIN_VALUE`가 대표적인 예시다.
### enum 타입으로 나타내기 적합한 경우
`enum` type으로 만들어 공개하면 된다.
### 이외의 경우
인스턴스화가 불가능한 유틸리티 클래스에 추가해 공개하면 된다.
유틸리티 클래스를 사용한다면, 해당 클래스의 이름까지 함께 명시해야 한다. (ex. `PhysicalConstants.AVOGADROS_NUMBER` )
또한, 정적 임포트(`static import`)를 통해 유틸리티 클래스의 이름을 생략할 수 있다.
### 추가) 숫자 리터럴의 밑줄(\_)
Java 7 부터 숫자 리터럴 사이에 `_` 을 사용할 수 있게 되었다.
이는 값에 영향없이 가독성을 높힐 수 있다.
십진수 리터럴을 세 자리씩 묶어주거나 한다면 좋을 것이다.
