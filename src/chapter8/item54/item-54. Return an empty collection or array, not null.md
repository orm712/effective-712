# null이 아닌, 빈 컬렉션이나 배열을 반환하라
## Null 반환 메서드
> - 컬렉션이나 배열 같은 컨테이너가 비었으면 `null`을 반환하는 메서드는, 항상 `방어 코드`를 넣어줘야 한다.
> - 즉, 클라이언트가 `null`상황을 처리하는 코드를 추가적으로 작성해야 하는 것이다.
```java
private final List<Cheese> cheesesInStock = ...;

public List<Cheese> getCheeses() {
	return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);
```

```java
// 방어 코드
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null && cheeese.contains(Cheese.STILTON))
	System.out.println("좋았어, 바로 그거야.");
```

## 빈 컬렉션 반환 메서드
> `null`대신 빈 컨테이너를 할당한다면, 방어 코드를 작성할 필요가 없어진다.
```java
public List<Cheese> getCheeses () {
	return new ArrayList<>(cheeseInStock);
```

### 빈 컬렉션이 성능을 떨어뜨리는 경우
> - 때로는 사용 패턴에 따라 빈 컬렉션 할당이 성능을 눈에 띄게 떨어뜨릴 수도 있다.
> - 이런 경우는 매번 똑같은 빈 `불변` 컬렉션을 반환하여 최적화가능하다.
> - 내부가 빈 불변 컬렉션
>   - `Collections.emptyList`, `Collections.emptySet`, `Collections.emptyMap`
```java
public List<Cheese> getCheese() {
	return cheesesInStock.isEmpty() ? 
    	Collections.emptyList() : new ArrayList<>(cheesesInStock);
}
```

## 빈 배열 반환 메서드
> 배열도 마찬가지로 `null`이 아닌 길이가 0인 배열을 반환하자.

> 길이가 0일수도 있는 배열을 반환하는 올바른 방법
```java
public Cheese[] getCheeses() {
	return cheesesInStock.toArray(new Cheese[0]);
}
```

> - 최적화 : 빈 배열을 매번 새로 할당 X
> - `List.toArray(T[] a)` 메서드는 주어진 배열 a가 충분히 크면 a안에 원소를 담아 반환하고, 그렇지 않다면 `T[]` 타입 배열을 새로 만들어 그 안에 원소를 담아 반환한다.
> - 따라서 원소가 하나라도 있다면 `Cheese[]` 타입 배열을 새로 생성해 반환하고, 원소가 0개면 EMPTY_CHEESE_ARRAY를 반환한다.
```java
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];

public Cheese[] getCheeses() {
	return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

## 핵심 정리
> - null이 아닌, 빈 배열이나 컬렉션을 반환하라.
> - null을 반환하는 API는 사용하기 어렵고 오류 처리 코드도 늘어나며, 성능이 좋은 것도 아니기 떄문이다.