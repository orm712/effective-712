# ITEM32. 제네릭과 가변인수를 함께 쓸 때는 신중하라
## 제네릭과 가변인수를 함께 쓸 때의 문제점
- 가변인수는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해준다.
```java
static void dangerous(List<String>... stringLists)
```
- 가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다.
  - 내부로 감춰야 했을 이 배열을 클라이언트로 노출하게 되면서 문제가 발생한다.
  - vararges 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.
  - Dangerous 참고
  - ITEM28. NotGenericArray은 바로 오류인데 왜 Dangerous는 경고일까?
    - 가변인수가 실무에서 매우 유용해서 설계자가 수용하기로 했다.
      - `Arrays.asList(T... a), Collections.addAll(Collection<? super T> c, T... elements)
        , EnumSet.of(E first, E... rest)`

## @SafeVarargs
- 자바 7부터 @SafeVarargs annotion이 추가되어 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다.
- 메서드가 타입 안전한 경우에만 이 annotation을 붙이자

## 타입 안전한 경우?
- 가변인수 메서드를 호출하면 varargs 매개변수를 담는 제네릭 배열이 만들어 진다.
- 메서드 내에서 이 배열에 아무것도 저장하지 않고, 배열의 참조가 밖으로 노출되지 않는다면 타입 안전하다.
- 순수하게 메서드의 생산자 역할(인수 전달)만 충실히 하면 메서드는 안전하다.


## 자신의 제네릭 매개변수 배열의 참조를 노출하는 것은 위험하다.
- PickTwo 참고

## 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다.
- 예외가 두 가지 있다.
  - @SafeVarargs annotation이 된 또 다른 varargs 메서드에 넘기는 것은 안전하다.
    - FlattenWithVarargs 참고
  - 이 배열 내용의 일부 함수를 호출만 하는(varargs를 받지 않는) 일반 메서드에 넘기는 것도 안전하다.
    - FlattenWithList 참고
    - 장점
      - 컴파일러가 이 메서드의 타입 안전성을 검증할 수 있다.
      - @SafeVarargs annotation을 생략해도 된다.
    - 단점
      - 클라이언트 코드가 복잡해진다.
  
## 정리
> 가변인수와 제네릭은 궁합이 좋지 않다.</br>
> 가변인수 기능은 배열을 노출하여 추상화가 완벽하지 못하고,
> </br>배열과 제네릭의 타입 규칙이 서로 다르다.
> </br>제네릭 varargs 매개변수는 타입 안전하지 않지만, 허용된다.
> </br>메서드 작성자가 타입 안전한지 확인하고, @SafeVarargs annotation을 달아야 한다.