# 7장. 람다와 스트릶

JAVA8부터 추가된 함수형 인터페이스, 람다, 메서드 참조 개념이 추가되었다. 스트림API도 추가되어 이 기능들에 대해서 다룬다.

# ITEM 42. 익명 클래스보다는 람다를 사용하라

예전 자바에서는 함수 타입을 표현할 때, 추상 메서드를 하나만 담은 인터페이스(가끔 추상 클래스로)를 사용했다.
과거에는 이렇게 사용했다.

```java

interface MyFunctionalInterface {
	void abstractMethod();
}

class MyClass implements MyFunctionalInterface {
	void abstractMethod() {
		// 구현부
	}
}
```

> 추상 메서드를 하나만 가지는 인터페이스를 오직 하나의 abstract 메서드를 가진 클래스로써 정의하고, 이로써 함수처럼 행동하는 인스턴스를 생성하도록 했습니다.

예시코드

예시 코드에서 Comparator 인터페이스가 정렬을 담당하는 추상 전략을 뜻하며, 문자열을 정렬하는 구체적 전략을 익명 클래스로 구현했다.
익명클래스방식의 문제점<< 너무 길다

자바8부터 추상 메서드 하나짜리 인터페이스는 특별한 존재가 되어 특별한 대우를 받게 되었다.
함수형 인터페이스라는 이름의 이 인터페이스는 람다식을 이용해 짧게 할 수 있다.

예시코드

- (SortFourWays.java 23L)

앞의 코드에서

- 람다는 Comparator<String>
- 매개변수 s1, s2는 String
- 반환값 int

이지만, 코드에서는 언급이 없다.
우리 대신 컴파일러가 문맥을 살펴 타입을 추론한다. 그래서 안익숙할때 람다보면 줜나헷갈린다.
상황에 따라 컴파일러도 헷갈려서 타입 추론을 못한다. 그러면 우리가 알려줘야한다.
타입 추론은 진짜 복잡해서 이 책의 챕터 하나를 써서 설명해야 할 정도이다. 그래서 이 규칙을 다 이해하는 프로그래머는 거의 없고, 잘 알지 못한다해도 상관없다.

> 타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입 명시는 생략하자

이렇게 하고 컴파일러가 타입을 몰?루겠어요 하면 알려주면된다.

람다 자리에 비교자 생성 메서드를 사용하면 이 코드를 더 간결하게 만들수있다.

- 29L

더 나아가 자바8에서 List인터페이스에 추가된 sort를 쓰면

- L34

람다를 언어 차원에서 지원하며 기존에는 적합하지 않았던 곳(익명인터페이스로구현하는거)에서도 함수객체를 실용적으로 사용할 수 있게 되었다.
아이템34(내가함ㅋㅋ)의 Operation 이넘을 보자.

- Operation.enum

저 동작을 람다를 쓰면?

- chapter5.item34

캬~ 이게야스지

이런 람다 기반 열거 타입을 보면 상수별 클래스 몸체는 더 이상 안쓸 것 같다고 느낄 지 모르지만, 그렇지는 않다.
메서드나 클래스와 달리 **람다는 이름이 없고 문서화가 안된다. 따라서 코드 자체로 동작이 명확하게 설명되지 않거나 코드 줄 수가 많아져서 이해하기 힘들고 가독성이 나락간다면 람다를 쓰면 안된다.**

람다는 한 줄 이면 가장 좋고, 세 줄을 넘어가면 가독성이 안좋아진다. 람다가 너무 길다면 간단히 줄여보거나 안쓰는 방향으로 리팩터링하자.

열거 타입 생성자에 넘겨지는 인수들의 타입도 컴파일타임에 추론된다. 따라서 열거 타입 생성자 안의 람다는 열거타입의 인스턴스 멤버에 접근 불가하다. (인스턴스는 런타임에 만들어진다.)
따라서 상수별 동작이 많이 길거나, 인스턴스 필드나 메서드를 내부에서 사용해야한다면 상수별 클래스 몸체를 사용해야 한다.

대 람다의 시대가 열리고 익명 클래스는 망했다. 근데 람다가 대체할 수 없는곳이 있다. 람다는 함수형 인터페이스에서만 쓰인다. 예를들어 추상 클래스의 인스턴스를 만들 때 람다를 못써서 익명클래스를 써야한다.
비슷하게 추상 메서드가 여러개인 인터페이스의 인스턴스 만들 때도 익명 클래스는 쓸수있고 람다는못쓴다.

마지막으로 람다는 자신을 참조할 수 없다. 람다에서 this하면 바깥클래스를 가리킨다. 면접에서이거대답하면 오 람다좀치는놈인가? 한다.
그래서 함수 객체가 자신을 참조해야하면 람다쓰면안되고 익명클래스해야한다.

또 마지막으로 람다도 직렬화형태가 구현별로, 가상머신별로 다를 수 있다. 그래서 직렬화하는거 조심해야한다. 그냥 안하는게좋다.

> 자바가 8로 업데이트되고 람다가 도입되었다. 익명클래스는 함수형 인터페이스가 아닌 티입의 인스턴스를 만들때만 사용하라. 람다는 작은 함수 객체를 아주 쉽게 표현할 수 잇어 함수형 프로그래밍의 지평을 열었다.
