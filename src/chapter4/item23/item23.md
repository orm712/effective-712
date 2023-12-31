# ITEM 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라

두 가지 이상의 의미를 표현할 수 있는 클래스인데 현재 표현하는 의미를 태그 등 값으로 알려주는 클래스 <- 쓰레기

## 쓰레기인 이유

### 태그가 없었다면 없어도 됄 것들

    1. 열거 타입 선언
    2. 태그 필드 `final Shape shape;`
    3. case문

### 다른 의미에 쓰기 위해서 안 쓰는 필드가 무조건 생긴다.

필드들을 final로 선언하려면 의미에 따라 사용되지 않는 필드도 초기화해야한다. 이걸 초기화 하는 코드가 불필요한데 또 추가된다.

### 다른 의미를 추가하려면 코드를 대거 뜯어고쳐야한다.

특히 밑에 `area()`메소드에 의미가 추가 될 때마다 case가 늘어나게 될 것이고, 이런 메소드가 한두개면 몰라도 메소드가 매우 많다면?? 그냥 지옥이다.
메소드 중 하나라도 빼먹는다면 바로 예외를 던질 것이므로 런타임에 프로그램이 터진다.

### (가장 쓰레기인점) 인스턴스의 타입 `Shape` 만으로는 이게 뭔지 알 수 없다.

결국 인스턴스 내부에 접근해서 `Shape` 필드를 확인해야 얘가 뭔지 알 수 있다.

---
결론 -> 태그 달린 클래스는 장황하고, 오류내기 쉽고, 비효율적이다.

다행히 자바와 같은 객체 지향 언어는 타입 하나로 다양한 의미의 객체를 표현할 수 있는 매우 좋은 수단을 제공한다.

## 클래스 계층구조를 이용한 subtyping

위에서 나온 태그 달린 클래스는 클래스 계층 구조를 어설프게 흉내냈을 뿐이다.

### 태그 달린 클래스를 계층구조로 바꾸는법

1. 계층구조의 root가 될 추상 클래스 정의
2. 태그 값에 따라 동작이 달라지는 메서드들을 root클래스 내의 추상 메서드로 선언
3. 태그값에 상관없이 동작이 일정한 놈들은 root클래스 내 일반 메서드로 구현
4. 모든 하위 클래스에서 사용하는 필드 전부 root클래스로
5. 루트 클래스 extends한 구체클래스를 의미별로 하나씩 정의

옆에 Figure를 바꿔보면?

이렇게 된다.

간결하고 명확하며, 쓸데없는 코드도 모두 사라졌다. 각 의미를 독립된 클래스에 담아 관련 없던 데이터필드를 모두 제거했다.

각 클래스의 생성자가 모든 필드르르 남김없이 초기화하고 추상 메서드를 모두 구현했는지 컴파일러가 확인해준다.
실수로 빼먹은 case문으로 런타임 에러가 발생할 일도 없다. 루트 클래스의 코드를 건드리지 않고 다른 프로그래머들이 독립적으로 계층구조를 확장하고 함께 사용 가능하다.
타입이 의미별로 따로 존재해서 변수의 의미를 명시하거나 제한 가능하고, 특정 의미만 매개변수로 받을 수 있다.

또한, 타입 사이의 자연스러운 계층 관례를 반영 가능해서 유연성은 물론 컴파일타임 타입 검사 능력을 높여준다는 장점도 있다.

새로 고친 계층형태에서 정사각형을 의미하는 클래스를 추가하려고 하면 어떻게 해야할까?


> 핵심 정리
>
> 태그 달린 클래스를 써야 하는 상황은 거의 없다. 새로운 클래스를 작성하는데 태그 필드가 등장하려고 하면 없애고 계층구조로 대체하는 방법을 생각해보자. 기존 클래스가 태그 필드를 사용하고 있다면 계층구조로
> 리팩터링하는걸 고민해보자.
