# 다른 타입이 적절하다면 문자열 사용을 피하라
> 문자열을 쓰지 않아야 할 상황들에 대해서 알아보자

## 문자열과 타입
### 1. 기본 타입
> - 문자열은 다른 값 타입을 대신하기에 적합하지 않다.
> - 예를 들어 키보드 입력으로부터 데이터를 받을 때, 실제 데이터가 수치형임에도 불구하고 문자열로 받는 경우가 흔하다.

> 받은 데이터가 수치형이라면, `int`, `float`, `BigInteger` 등 적당한 수치 타입으로 변환해야 한다.

### 2. 열거 타입
> - 상수를 열거할 때는 문자열보다 열거 타입이 월등히 낫다.
> - [item34](../../chapter6/item34/item-34.%20Use%20enums%20instead%20of%20int%20constants.md)

### 3. 혼합 타입
> - 문자열은 혼합 타입을 대신하기에 적합하지 않다.
> - 즉, 여러 요소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 대체로 좋지 않은 생각이다.
```java
String compoundKey = className + "#" + i.next();
```
> - 예시와 같이 혼합 타입을 문자열로 처리한다면, 각 요소를 개별로 접근하려 할 때 문자열을 파싱해야 해서 느리고 오류 가능성도 커진다.
> - 무엇보다 적절한 `equals()`, `toString()`, `compareTo()` 메서드를 제공할 수 없고, String이 제공하는 기능에만 의존해야 한다.

> 해결 방법은, 전용 클래스를 `private` 정적 멤버 클래스로 새로 만들면 된다.
```java
public class A {
	private static class B {
    ...
    }
}
```

## 문자열과 권한
> 문자열은 권한(capacity)을 표현하기에 적합하지 않다.

> - 예를 들어, 각 스레드가 자신만의 변수를 갖게 해주는 스레드 지역변수 기능을 설계한다 해보자.
>   - 흔한 방법은 클라이언트가 제공한 문자열 키로 스레드별 지역변수를 식별하는 것이다.

### 문자열로 권한 부여
```java
public class ThreadLocal {
	private ThreadLocal() {} // 객체 생성 불가
    
    // 현 스레드의 값을 키로 구분해 저장
    public static void set(String key, Object value);
    
    // (키가 가리키는) 현 스레드의 값을 변환하다.
    public static Object get(String key);
    
    //...
```

> - 이 방식의 문제는 스레드 구분용 문자열 키가 전역 이름공간에서 공유된다는 점이다.
> - 즉, 클라이언트가 고유한 키가 아닌 소통의 부재로 같은 키를 사용하게 된다면, 의도치 않게 변수를 공유하게 된다.

### Key 클래스로 권한 부여
```java
public class ThreadLocal {
	private ThreadLocal() { }
    
    public static class Key {
    	Key() { }
    }
   
    // 위조 불가능한 고유 키를 생성한다.
    public static Key getKey() {
   	   return new Key();
    }
   
    public static void set(Key key, Object value);
    public static Object get(Key key);
```

> - 이제 `set`과 `get`은 정적 메서드일 이유가 없어지니 인스턴스 메서드로 바꿔도 된다.
> - 이렇게 되면 `Key`는 더 이상 스레드 지역변수를 구분하기 위한 키가 아니라, 그 자체가 스레드 지역변수가 된다.
>   - 따라서 할 일이 없어지는 `ThreadLocal`은 없애버리고 `Key`의 이름을 `ThreadLocal`로 바꿀 수 있다.

### Key를 ThreadLocal로 변경 후 매개변수화
```java
public final class ThreadLocal<T> {
	public ThreadLocal();
    public void set(T value);
    public T get();
```

## 핵심 정리
> 더 적합한 데이터 타입이 있거나 새로 작성할 수 있다면, 문자열은 쓰지 말자. 문자열은 잘못 사용하면 번거롭고, 덜 유연하며 느리고 오류 가능성도 크다.