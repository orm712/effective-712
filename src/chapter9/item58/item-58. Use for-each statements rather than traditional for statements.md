# 전통적인 for 문보다는 for-each 문을 사용하라

## 전통적인 for문의 약점
```java
for (Iterator<Element> i = c.iterator(); i.hasNext()) {
  Element e = i.next();
  //...
}
```

```java
for (int i=0; i<a.length; i++) {
    //...
}
```
> - `while`보다는 낫지만, 그래도 여전히 인덱스가 잘못 쓰일 위험이 있다.
>   - 인덱스를 굳이 쓰지 않는다면, 외부로 노출시킬 필요는 없다.

> 결국 `for` 문보다 `for-each`문을 사용하라는 이유는 사용하지 않는 것들을 공개해서 괜히 실수를 유발하지 말라는 것과 같다. 방어적으로 코딩해서 안전한 코드를 만들자는 철학이다.

## for-each 문 소개
```java
for (Element e: elements) {
    //..
}
```
> - 간단하게 반복을 구성할 수 있다.
>   - 그렇다고 속도도 느리지 않고, 최적화된 속도를 따른다
> - 컬렉션 중첩에서는 더욱 이점이 커진다.
>   - `Iterator`를 건드릴 필요도 없기 때문에 실수할 일도 적다.

## for문을 잘못 사용했을 때 생기는 버그 찾기
[ForTest_Wrong.java](ForTest_Wrong.java)
> - 먼저, 사용자가 `i.next()`와 `j.next()`를 이용하여 작성하고자 한 코드는 이것이 아니었을 것이다.
> - 외부 루프에 이용된 `enum Suit`가 `enum Rank`보다 짧아서 `NoSuchElementException`을 던지게 된다.
>   - 예외가 뜨지 않아서 결과값을 눈으로 확인하기 전까지 코드가 잘못되었는지 알기 힘들다.
>   - [ForTest_Correct.java](ForTest_Correct.java)

> - `for-each`를 통해 훨씬 깔끔한 방향으로 코드를 개선할 수 있다.
>   - `for-each`를 이용하면 순회 코드에서 생기는 실수를 이전에 방지할 수 있다.
>   - [ForEachTest.java](ForEachTest.java)

## for-each를 사용할 수 없는 경우
> - 파괴적인 필터링
>   - 반복을 돌며 원소를 하나씩 지워나가는 필터링
>   - `Collection`의 `removeIf()`를 통해 구현해 나가자
>   - 특정 조건을 만족하는 원소를 지우는 메소드
> - 변형
>   - 리스트 혹은 배열을 순회하며 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용하자
> - 병렬 반복
>   - 여러 컬렉션을 병렬로 순회해야 한다면, 반복자와 인덱스 변수를 사용해 엄격하게 제어하는 편이 좋다.

## Iterable 인터페이스
```java
public interface Iterable<E> {
  Iterator<E> iterator();
}
```
> - for-each를 사용하기 위해서는 Iterable을 구현해야 한다.
> - Iterable만 간단히 구현하면, `for-each`를 사용할 수 있다.
>   - Iterable 인터페이스를 구현한 클래스는 iterator() 메서드를 제공해야 하며, 이 메서드는 Iterator를 반환한다.
```java
for (ElementType element : iterable) {
    // element를 사용한 코드
}
```
> 위 코드는 컴파일러에 의해 아래와 같이 변환된다.
```java
for (Iterator<ElementType> it = iterable.iterator(); it.hasNext(); ) {
    ElementType element = it.next();
    // element를 사용한 코드
}
```
> 따라서, for-each문은 Iterable 인터페이스를 구현한 객체만 사용할 수 있으며, 이를 통해 직접 Iterator를 생성하고 순회하는 번고로움을 줄일 수 있다.


## 핵심 정리
> - 따로 반복자에 접근할 일이 없고, for문을 이용하는 것이 유리한게 아니라면, `for-each`를 우선적으로 고려하자.
