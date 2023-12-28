# ITEM 30. 이왕이면 제네릭 메서드로 만들라
## 클래스와 마찬가지로, 메서드도 제네릭으로 만들 수 있다.
- 매개변수화 타입을 받는 정적 유틸리티 메서드는 보통 제네릭이다.
  - 다이아몬드 연산자 안에 Type Parameter넣는 방법
  - Collections의 '알고리즘 메서드'
    - binarySearch, sort 등 모두 제네릭
  - 제네릭 메서드는 제네릭 타입과 작성법이 비슷하다.
  - RawType, GenericType 참고

## 불변 객체를 여러 타입으로 활용해야 될 때
- 제네릭은 런타임에 타입 정보가 `소거`되므로 하나의 객체를 어떤 타입으로든 매개변수화할 수 있다.
- 하지만 이렇게 하려면, 요청한 타입 매개 변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다.
- 이 패턴을 제네릭 싱글턴 팩터리라고 한다.(아이템 42)
  - Collections.reverseOrder, Collections.emptySet 등

## 항등함수(identity function)
- 입력 값 수정 없이 그대로 반환하는 함수
- Function.identity를 사용하면 된다고 한다.
- 공부 목적으로 직접 작성
- 항등함수 객체는 상태가 없으니 요청할 때마다 새로 생성하는 것이 낭비다.
  - 내부적으로 어떠한 데이터도 저장하거나 변화시키지 않고 입력 값 그대로 반환
    - 제네릭 싱글턴 팩토리 패턴을 사용하면 된다.
    - GenericSingletonFactory 참고

## 재귀적 타입 한정(recursive type bound)
- 드문 경우지만, 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정할 수 있다.
- 주로 Comparable 인터페이스와 같이 사용된다.
  ```java
    public interface Comparable<T> {
      int compareTo(T o);
    }
    ```
  - 그래서 자신과 같은 타입의 원소와만 비교할 수 있다.
    - String이 `Comparable<String>`을 구현하고 Integer가 `Comparable<Integer>`를 구현한다.
- RecursiveTypeBound 참고

## 정리
> 제네릭 타입과 마찬가지로, 클라이언트에서 입력 매개변수와 반환값을 명시적으로 형변환해야 하는 메서드보다 제네릭 메서드가 더 안전하며 사용하기도 쉽다.