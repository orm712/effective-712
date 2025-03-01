# 추상 클래스보다는 인터페이스를 우선하라

> - 자바 8부터는 인터페이스도 디폴트 메서드를 제공할 수 있게 되어 인터페이스와 추상 클래스 모두 인스턴스 메서드를 구현 형태로 제공할 수 있다.
> - 둘의 가장 큰 차이는 추상 클래스가 정의한 타입을 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 한다.
>   - 자바는 단일 상속만 지원하니 큰 제약이다.
> - 인터페이스는 선언한 메서드를 모두 정의하고 그 규약을 지킨 클래스라면 다른 어떤 클래스를 상속해도 같은 타입으로 취급한다.

## 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해 넣을 수 있다.
> - 실제로 `Comparable`, `Iterable`, `AutoCloseable`등 새로운 인터페이스를 수 많은 기존 클래스가 이 인터페이스를 구현한 채 릴리즈됐다.
> - 하지만 기존 클래스에 새로운 추상 클래스를 끼워 넣기는 어렵다. 두 클래스가 같은 추상 클래스를 확장하려면, 그 추상 클래스는 계층 구조상 두 클래스의 공통 조상이어야 한다.

## 인터페이스는 믹스인 정의에 안성맞춤이다.
> - 믹스인은 클래스가 구현할 수 있는 타입으로, 클래스의 원래 `주된 타입`외에도 다른 특정 선택적 행위를 제공한다고 선언하는 것이다.
> - 예를 들어 `Comparable`을 구현한 클래스는 자신을 구현한 인스턴스끼리 정렬이 가능하다고 선언하는 것이다.
> - 추상 클래스는 앞서 말했듯이 덧씌우기 어렵고, 두 부모를 가지지 못하므로 불가능하다.

## 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.
```java
public interface Singer {
    AudioClip sing(Song s);
}

public interface Songwriter {
    Song compose(int chartPosition);
}
```

> 만약 작곡도 하고 노래도 한다면 두 인터페이스 모두를 구현해도 된다. 심지어 모두를 extends 하고, 새 제 3의 메서드도 추가 가능하다.

```java
public interface SingerSongwriter extends Singer, Songwriter {
    AudioClp strum();
    void actSensitive();
}
```

> - 만약 이걸 클래스로 만들려면 가능한 조합의 수를 모두 각각 클래스로 정의해야 한다.
> - 속성이 n개라면 조합의 수는 2^n개이다.

## 인터페이스는 기능을 향상 시키는 안전하고 강력한 수단이 된다.
> - 타입을 추상 클래스로 정의해두면 그 타입에 기능을 추가하는 방법은 상속뿐이다.
> - 상속해서 만든 클래스는 래퍼 클래스보다 활용도가 떨어지고 깨지기 쉽다.

> - 인터페이스의 메서드 중 구현 방법이 명백한 것이 있다면, 디폴트 메서드로 만들 수 있다.
> - 하지만, 디폴트 메서드는 제약이 있다.
>   - equals와 hashcode를 디폴트 메소드로 제공 안한다.
>   - 인터페이스는 인스턴스 필드를 가질 수 없고, private 정적 메소드를 가질 수 없다.
>   - 본인이 만든 인터페이스가 아니면 디폴트 메소드 추가가 불가능하다.

## 인터페이스와 추상 골격 구현 클래스
> 인터페이스와 추상 골격 구현 클래스를 함께 제공해서 인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다.

> - 인터페이스로는 타입을 정의하고, 디폴트 메서드도 몇 개 제공한다.
> - 그리고 골격 구현 클래스는 나머지 메서드들까지 구현한다.
> - 이렇게 하면 골격 구현을 확장하는 것만으로 이 인터페이스를 구현하는데 필요한 일이 대부분 완료된다.
>   - 템플릿 메서드 패턴이라고 한다.
> - 골격 구현 클래스는 추상 클래스처럼 구현을 도와주는 동시에, 추상 클래스로 타입을 정의할 때 따라오는 심각한 제약에서 자유롭다는 것이 장점이다.

> - 골격구현 클래스를 사용하지 않은 반복적인 코드 사용
>   - [Athlete.java](Athlete.java)
> - 추상 골격 구현 클래스를 사용한 예시
>   - [Athlete2.java](Athlete2.java)
>   - 디폴트 메서드를 사용하지 않고 추상 골격 구현 클래스를 구현하여 중복을 없앨 수 있다.
>   - 일반적으로 다중 구현용 타입으로는 인터페이스가 가장 적절하며 재사용성 측면이나 유연성 측면, 다형성 측면에서 인터페이스를 우선하는 것이 옳다.
