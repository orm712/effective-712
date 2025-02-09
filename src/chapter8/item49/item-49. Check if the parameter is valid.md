# 매개변수가 유효한지 검사하라

메서드와 생성자 대부분은 입력 매개변수의 값이 특정 조건을 만족하기를 바란다. 객체 참조는 null이 아니어야 하는 식이다.

이런 제약은 반드시 분서화해야 하며 메서드 몸체가 시작되기 전에 검사해야 한다.

> - 메서드 몸체가 실행되기 전에 매개변수를 확인한다면 즉각적이고 깔끔한 방식으로 예외를 던질 수 있다.
>   - 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.
>   - 더 나쁜 상황으로 메서드가 잘 수행되지만 잘못된 결과를 반환하게 된다.
>   - 한층 더 나쁜 상황은 메서드는 문제없이 수행됐지만, 어떤 객체를 이상한 상태로 만들어 놓아서 미래의 알 수 없는 시점에 이 메서드와는 관련 없는 오류를 낼 때다.

## public, protected 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화해야 한다.
> - @throws 자바독 태그를 사용하자
> - 보통은 IllegalArgumentException, IndexOutOfBoundsException, NullPointerException 중 하나가 될 것이다.

```java
/**
 * (현재 값 mod m) 값을 반환한다. 이 메서든느
 * 항상 음이 아닌 BigInterger를 반환한다는 점에서 remainder 메서드와 다르다.
 *
 * @param m 계수(양수여야 한다.)
 * @return 현재 값 mod m
 * @throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
 */
public BigInteger mod(BigInteger m) {
   if (m.signum() <= 0)
      throw new ArithmeticException("계수(m)는 양수여야 합니다. " + m);
   ... // 계산 수행
}
```
> - 이 메서드는 m이 null이면 m.signum() 호출 때 NullPointerException을 던진다.
> - 그런데 해당 설명은 어디에도 없다.
>   - 그 이유는 이 설명을 BigInteger 클래스 수준에서 기술했기 때문이다.
>   - 훨씬 깔끔하다.

> - 자바 7에 추가된 `java.util.Objects.requireNonNull` 메서드는 유연하고 사용하기도 편하니, 더 이상 null 검사를 수동으로 하지 않아도 된다.
> - 원하는 예외 메시지도 지정 가능.
> - 또한, 입력을 그대로 반환하므로 값을 사용하는 동시에 null 검사를 수행할 수 있다.

```java
this.strategy = Objects.requireNonNull(strategy, "전략");
```
> 어디서든 순수한 null 검사 목적으로 사용해도 된다.

> - 자바 9에는 Objects에 범위 검사 기능도 더해졌다고 한다.
>   - checkFromIndexSize, checkFromToIndex, checkIndex라는 메서드들이다.

> - 공개되지 않은 메서드라면 패키지 제작자인 개발자가 메서드를 호출되는 상황을 통제할 수 있다.
> - 따라서 오직 유효한 값만이 메서드에 넘겨지리라는 것을 여러분이 보증할 수 있고, 그렇게 해야 한다.
> - 다시 말해 public이 아닌 메서드라면 단언문(assert)을 사용해 매개변수 유효성을 검증할 수 있따.

```java
private static void sort(long a[], int offset, int length) {
   assert a != null;
   assert offset >= 0 && offset <= a.length;
   assert length >= 0 && length <= a.length - offset;
   ... // 계산 수행
}
```
> - 단언문은 몇 가지 면에서 일반적으로 유효성 검사와 다르다.
> - 실패하면 AssertionError를 던진다.
> - 두 번째, 런타임에 아무런 효과도, 아무런 성능 저하도 없다.

> - 메서드가 직접 사용하지는 않으나 나중에 쓰기 위해 저장하는 매개변수는 더 신경써서 검사해야 한다.
>   - 나중에 추적하기 어려워 디버깅이 훨씬 어려워진다.

## 생성자는 "나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라"는 원칙의 특수한 사례다. 생성자 매개변수의 유효성 검사는 클래스 불변식을 어기는 객체가 만들어지지 않게 하는 데 꼭 필요하다.
> - 유효성 검사 비용이 지나치게 높거나 실용적이지 않을 떄, 혹은 계산 과정에서 암묵적으로 검사가 수행될 때 예외가 있다.
>   - Collections.sort(List)
>     - 리스트 안의 객체들은 정렬 과정에서 이 비교가 이뤄진다.
>     - 비교될 수 없는 객체가 들어 있다면 비교할 때 Class CastException을 던질 것이다.
>     - 따라서 비교하기 앞서 모든 객체가 상호 비교될 수 있는지 검사해봐야 별다른 실익이 없다.

## 정리
> - 메서드나 생성자를 작성할 때면 그 매개변수들에 어떤 제약이 있을 시 생각해야 한다.
> - 그 제약들을 문서화하고 메서드 코드 시작 부분에서 명시적으로 검사해야 한다.
>   - 이런 습관을 반드시 기르자.
