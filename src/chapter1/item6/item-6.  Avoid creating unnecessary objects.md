# 불필요한 객체 생성을 피해라

- 같은 기능의 객체를 새로 생성하는 대신, 객체 하나를 재사용하는 편이 나을 때가 많다.
- 특히, 불변 객체는 언제든 재사용할 수 있다.

## 문자열 객체 생성
> - 같은 값임에도 다른 레퍼런스인 경우
>   - 기존의 인스턴스를 재사용하자.
```java
String s = new String("bikini");
```
- String을 new로 생성하면 항상 새로운 객체를 만들게 된다.
```java
String s = "bikini";
```
- 이 코드는 새로운 인스턴스를 매번 만드는 대신 하나의 String 인스턴스를 재사용한다.
- 같은 가상 머신 안에서 이와 똑같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다.

## static factory 메서드 사용하기
- 생성자대신 정적 팩터리 메서드를 제공하는 불변 클래스에서는 불필요한 객체 생성을 피할 수 있다.
- ex) `Boolean(String)` 생성자 대신 `Bollean.valueOf(String)` 팩터리 메서드 사용

```java
Boolean true1 = Boolean.valueOf("true");
Boolean true2 = Boolean.valueOf("true");

System.out.println(true1 == true2); // true
```

## 생성 비용이 아주 비싼 객체
> - 만드는 데 메모리나 시간이 오래 걸리는 `비싼 객체`를 반복적으로 만들어야 한다면 캐싱해두고 재사용할 수 있는지 고려하자

```java
static boolean isRomanNumeralSlow(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
            + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```
> - `String.matches`는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만, 성능이 중요한 상황에서 반복해서 사용하기 적합하지 않다.
>   - 내부에서 만드는 정규 표현식용 `Pattern`인스턴스는 한 번 쓰고 버려져서 곧바로 가비지 컬렉션 대상이 된다.
>   - `Pattern`은 생성비용이 높은 클래스 중 하나이다.
>   - 상수(`static final`)로 초기에 캐싱해놓고 재사용하자.
```java
public class RomanNumerals {
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})"
                    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeralFast(String s) {
        return ROMAN.matcher(s).matches();
    }
}
```
> - `Pattern`인스턴스를 클래스 초기화(정적 초기화)과정에서 직접 생성해 캐싱해두고, 나중에 `isRomanNumeral`이 호출될 때마다 이를 재사용한다.

## 어댑터
- 불변 객체인 경우에 재사용하면 안전함이 명백하다.
  - 하지만, 몇몇 경우 분명하지 않다.
- 어댑터는 인터페이스를 통해 뒤에 있는 객체로 연결해주는 view라서 여러 개를 만들 필요가 없다.

> - 같은 인스턴스를 대변하는 여러 개의 인스턴스를 생성하지 말자.
```java
Map<String, Object> map = new HashMap<>();
map.put("Hello", "World");

Set<String> set1 = map.keySet();
Set<String> set2 = map.keySet();

assertThat(set1).isSameAs(set2); // TRUE

set1.remove("Hello");
System.out.println(set1.size()); // 1
System.out.println(set1.size()); // 1
```

> - Map 인터페이스의 `keySet`메서드는 Map 객체안의 키 전부를 담은 `Set` 인터페이스의 뷰를 반환한다.
> - 동일한 Map에서 호출하는 `keySet`메서드는 `같은 Map`을 대변하기 때문에 반환한 객체 중 하나를 수정하면 다른 모든 객체가 따라서 바뀐다.
>   - 따라서 `keySet`이 뷰 객체 여러 개를 만들 필요도 없고 이득도 없다.

## 오토박싱
> - 기본 타입과 박싱된 기본 타입을 섞어 쓸 때 자동으로 상호 변환해주는 기술.
```java
private static long sum() {
	Long sum = 0L;
	for(long i=0; i<=Integer.MAX_VALUE; i++) {
		sum += i;
	}
	return sum;
}
```
> - sum 변수를 `long`이 아닌 `Long`으로 사용해서 불필요한 `Long`인스턴스가 약 2의 31승 개나 만들어졌다.
> - 박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않는 오토박싱이 숨어들지 않도록 주의하자.

## 오해 금지
> - `객체 생성은 비싸니 피해야 한다`로 오해하면 안 된다.
> - 요즘 JVM에서는 별 다른 일을 하지 않는 작은 객체를 생성하고 회수하는 일이 크게 부담되지 않는다.
>   - 프로그램의 명확성, 간결성, 기능을 위해 객체를 추가 생성하는 것은 좋은 것이다.
> - 단순히 객체 생성을 피하기 위해 자신만의 객체 풀을 만들지 말자.
>   - DB 커넥션 풀 같은 경우 생성 비용이 워낙 비싸니 재사용하는 게 낫지만, 일반적으로 자체 객체 풀은 코드를 헷갈리게 하고, 메모리 사용량을 늘리고, 성능을 떨어뜨린다.
>   - 요즘 JVM의 GC는 잘 최적화되어 있어서, 가벼운 객체를 다룰 때는 직접 만든 객체 풀보다 훨씬 빠르다.
