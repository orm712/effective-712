# 지연 초기화는 신중히 사용하라
## 지연 초기화(lazy initialization)
> - 필드의 `초기화 시점`을 그 값이 처음 필요할 때까지 늦추는 기법
>   - 값이 전혀 쓰이지 않으면, 초기화도 절대 일어나지 않는다.
> - 정적 필드와 인스턴스 필드 `모두 적용 가능`
> - 주로 `최적화 용도`로 쓰이지만, 클래스와 인스턴스 초기화 때 발생하는 위험한 순환 문제를 해결하는 효과도 있다.
> - 지연 초기화는 양날의 검이다.
>   - 클래스 혹은 인스턴스 생성 시 초기화 비용은 줄어들지만 지연 초기화하는 필드에 접근하는 비용은 커진다.
>   - 지연 초기화를 하는 필드 중 결국 초기화가 이뤄지는 비율에 따라, 실제 초기화에 드는 비용에 따라, 초기화된 각 필드를 얼마나 빈번히 호출하느냐에 따라 지연 초기화가 실제로는 성능을 느려지게 할 수도 있다.
> - 지연 초기화가 필요한 경우
>   - 해당 클래스의 인스턴스 중 그 필드를 사용하는 인스턴스의 비율이 낮은 반면, 그 필드를 초기화하는 비용이 클 때
> - 멀티 쓰레드 환경에서는 지연 초기화를 하기 까다롭다.
>   - 지연 초기화하는 필드를 둘 이상의 쓰레드가 공유한다면 어떤 형태로든 반드시 동기화해야 한다.
> - `대부분의 상황에서 일반적인 초기화가 지연 초기화보다 낫다.`

### ex1) 인스턴스 필드를 선언할 때 수행하는 일반적인 초기화 예시
```java
private final FieldType field = computeFieldValue();
```

### ex2) synchronized 접근자를 이용한 인스턴스 필드의 지연 초기화 예시
```java
private FieldType field;
private synchronized FieldType getField() {
  if (field == null)
    field = computeFieldValue();
  return field;
}
```
> - 지연 초기화가 초기화 순환성을 깨뜨릴 것 같으면 synchronized 접근자를 사용하자
> - 이상의 두 관용구는 정적 필드에도 똑같이 적용된다.
>   - 물론, 필드와 접근자 메서드 선언에 static 한정자를 추가해야 한다.

### ex3) 정적 필드용 지연 초기화 홀더 클래스 관용구 예시
```java
private static class FieldHolder {
  static final FieldType field = computeFieldValue();
}

private static FieldType getField() { return FieldHolder.field; }
```
> - 성능 때문에 정적 필드를 지연 초기화해야 한다면 `지연 초기화 홀더 클래스 관용구`를 사용하자
> - 클래스는 클래스가 `처음 쓰일 때 비로소 초기화`된다는 특성을 이용한 관용구이다.
> - getField가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서, 비로소 FieldHolder 클래스 초기화를 촉발한다.
> - getField 메서드가 필드에 접근하면서 동기화를 전혀하지 않으니 성능이 느려질 거리가 없다.
> - 일반적인 VM은 오직 클래스를 초기화할 때만 필드 접근을 동기화한다.
> - 클래스 초기화가 끝난 후에는 VM이 동기화 코드를 제거하여, 그 다음부터는 아무런 검사나 동기화 없이 필드에 접근한다.

### ex4) 인스턴스 필드 지연 초기화용 이중검사 관용구 예시
```java
private volatile FieldType field;

private FieldType getField4() {
  FieldType result = field;
  if (result != null)    // 첫 번째 검사 (락 사용 안 함)
    return result;

  synchronized(this) {
    if (field == null) // 두 번째 검사 (락 사용)
      field = computeFieldValue();
    return field;
  }
}
```
> - 성능 때문에 `인스턴스 필드`를 지연 초기화해야 한다면 이중검사 관용구를 사용하자
> - 이 관용구는 `초기화된 필드`에 접근할 때 동기화 비용을 없애준다.
> - 필드의 값을 두 번 검사하는 방식으로, 한 번은 동기화 없이 검사하고, 두 번째는 동기화하여 검사한다.
> - 두 번째 검사에서도 필드가 초기화되지 않았을 때만 필드를 초기화한다.
> - 필드가 초기화된 후로는 동기화하지 않으므로 해당 필드는 반드시 `volatile`로 선언해야 한다.
>   - 객체 생성과 참조 쓰기 사이의 CPU·컴파일러 재배치를 막아 `초기화가 완전히 끝난 객체`만 노출되도록 보장한다.
> - result 지역 변수가 필요한 이유
>   - 필드가 이미 초기화된 상황(일반적)에서는 그 필드를 딱 한 번만 읽도록 보장하는 역할

### ex5) 단일 검사 관용구 (초기화가 중복해서 일어날 수 있다)
```java
private volatile FieldType field;

private FieldType getField() {
  FieldType result = field;
  if (result == null)
    field = result = computeFieldValue();
  return result;
}

private static FieldType computeFieldValue() {
  return new FieldType();
}
```
> - 이중 검사의 변종 관용구이다.
> - 반복해서 초기화해도 상관없는 인스턴스 필드를 지연 초기화하는 경우, 초기화가 중복해서 일어날 수 있다.

## 정리
> - 대부분의 필드는 지연시키지 않고 바로 초기화해야 한다.
> - 성능 때문에 혹은 위험한 초기화 순환을 막기 위해서 지연 초기화를 쓴다면 올바르게 사용해야 한다.
> - 인스턴스 필드에는 이중검사 관용구를, 정적 필드에는 지연 초기화 홀더 클래스 관용구를 사용하자.
> - 반복해 초기화해도 괜찮은 인스턴스에는 단일검사 관용구도 고려해보자.