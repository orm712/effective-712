# 전통적인 for 문보다는 for-each 문을 사용하라

## 전통적인 for문의 약점
```java
for (Iterator<Element> i = c.iterator(); i.hasNext()) {
  Element e = i.next();
  
  
  
}
```

```java
for (int i=0; i<a.length; i++) {
    
}
```
> - `while`보다는 낫지만, 그래도 여전히 인덱스가 잘못 쓰일 위험이 있다.
>   - 인덱스를 굳이 쓰지 않는다면, 외부로 노출시킬 필요는 없다.

> 결국 `for` 문보다 `for-each`문을 사용하라는 이유는 사용하지 않는 것들을 공개해서 괜히 실수를 유발하지 말라는 것과 같다. 방어적으로 코딩해서 안전한 코드를 만들자는 철학이다.

## for-each 문 소개
```java
for (Element e: elements) {
    
}
```
> - 간단하게 반복을 구성할 수 있다.
>   - 그렇다고 속도도 느리지 않고, 최적화된 속도를 따른다
> - 컬렉션 중첩에서는 더욱 이점이 커진다.
>   - `Iterator`를 건드릴 필요도 없기 때문에 실수할 일도 적다.

## for문을 잘못 사용했을 때 생기는 버그 찾기
```java
enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING }

@Test
@DisplayName("NoSuchElementException 을 던지는 예제")
public void test() {
    Collection<Suit> suits = Arrays.asList(Suit.values());
    Collection<Rank> ranks = Arrays.asList(Rank.values());

    Assertions.assertThrows(NoSuchElementException.class, () ->{
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();) {
                System.out.println(i.next() + ", " + j.next());
            }
        }
    });
}
```
> - 먼저, 사용자가 `i.next()`와 `j.next()`를 이용하여 작성하고자 한 코드는 이것이 아니었을 것이다.
> - 외부 루프에 이용된 `enum Suit`가 `enum Rank`보다 짧아서 `NoSuchElementException`을 던지게 된다.
>   - 예외가 뜨지 않아서 결과값을 눈으로 확인하기 전까지 코드가 잘못되었는지 알기 힘들다.

```java
@Test
@DisplayName("정상적으로 동작하는 케이스 but 코드가 복잡해보인다.")
public void test3() {
    Collection<Face> faces = EnumSet.allOf(Face.class);

    for (Iterator<Face> i = faces.iterator(); i.hasNext();) {
        Face elementI = i.next();
        for (Iterator<Face> j = faces.iterator(); j.hasNext();) {
            System.out.println(elementI + ", " + j.next());
        }
    }
}
```

```java
@Test
@DisplayName("for-each의 사용으로 코드가 훨씬 깔끔해진다.")
public void test4() {
    Collection<Face> faces = EnumSet.allOf(Face.class);

    for (Face face1 : faces) {
        for (Face face2 : faces) {
            System.out.println(face1 + ", " + face2);
        }
    }
}
```
> - `for-each`를 통해 훨씬 깔끔한 방향으로 코드를 개선했다.
>   - `for-each`를 이용하면 순회 코드에서 생기는 실수를 이전에 방지할 수 있다.

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

## 핵심 정리
> - 따로 반복자에 접근할 일이 없고, for문을 이용하는 것이 유리한게 아니라면, `for-each`를 우선적으로 고려하자.
