# item26. 로 타입은 사용하지 말라 
## 제네릭 타입
### 제네릭 클래스 / 인터페이스
> 클래스와 인터페이스 선언에 `타입 매개변수(type parameter)`가 쓰인 것
> </br>ex) `List<E>`, `Map<K,V>` -> `List`, `Map`라고 짧게 표현
> </br>제네릭 클래스와 제네릭 인터페이스를 통틀어 `제네릭 타입`이라고 한다.
</br>

### 매개변수화 타입
> 제네릭 타입은 `매개변수화 타입`을 정의한다.
> </br>ex) `List<String>` -> `String` 이 정규 타입 매개변수 E에 해당하는 `실제 타입 매개변수`

### 로 타입
> 제네릭 타입에서 타입 매개변수를 `전혀 사용하지 않을 때`
> </br>ex) `List<E>` -> `List`
> </br>타입 선언에서 제네릭 타입 정보가 전부 지워진 것처럼 동작하는데,</br>
> `제네릭이 나오기 전에` 코드와 호환되도록 하기 위해 나왔던 것
```java
private final Collection stamps = ...;
stamps.add(new Coin(...)); 
```
>실수로 Stamp가 아닌 Coin을 넣어도 잘만 컴파일되고 실행된다.(unchecked call 경고가 나오긴 함)
></br> stamps에서 coin을 꺼내면 오류가 걸림
```java
private final Collection<Stamp> stamps = ...;
```
> 이렇게 제네릭을 활용하면 add 할때 컴파일 에러가 난다.</br>
> 원소를 꺼낼때도 보이지 않는 형변환을 추가하여 절대 실패하지 않는다.</br>
> 예시가 억지같긴한데, 현업에서도 종종 일어나는 오류니까 로 타입을 절대 쓰면 안된다.</br>
> `로 타입을 쓰면 제네릭이 안겨주는 안전성과 표현력을 모두 잃게 된다.`

### 그럼 로 타입을 왜 만듦?
> `호환성`때문이다.</br>
> 자바가 만들어지고 제네릭이 나오기까지 10년이 걸렸다.</br>
> 로 타입을 사용하는 메서드에 매개변수화 타입의 인스턴스를 넘겨도(반대도) 동작해야 했다.</br>
> 이 마이그레이션 호환성을 위해 로 타입을 지원하고 제네릭 구현에는 `소거`(아이템28) 방식을 이용하기로 했다.</br>
> `List`같은 로 타입은 사용해서는 안되나, `List<Object>`처럼 임의 객체를 허용하는 매개변수화 타입은 괜찮다.</br>

### ? 소거?
> `Generic Type erasure`라고 하는데,
> </br> 원소 타입을 컴파일 타입에만 검사하고 `런타임에는 해당 타입 정보를 알 수 없는 것`
> </br> 컴파일 타임에만 타입 제약 조건을 정의하고, 런타임에는 타입을 제거한다는 것

### `List` vs `List<Object>`
> `List`는 제네릭 타입이 완전히 아닌 것이고, `List<Object>`는 모든 타입을 허용한다는 의사를 컴파일러에 명확히 전달한 것
> </br>`List`를 받는 메서드에 `List<String>`을 넘길 수 있지만, `List<Object>`를 받는 메서드에는 넘길 수 없다.</br>
> `제네릭의 하위 타입 규칙` 때문이다.</br>
> `List<String>`은 `List`의 하위 타입이지만, `List<Object>`의 하위 타입은 아니다.</br>
> `List<Object>`같은 매개변수화 타입을 사용할 때와 달리 `List` 같은 로 타입을 사용하면 타입 안정성을 잃게 된다.</br>
> 그러면 우린 `비한정적 와일드카드 타입`을 쓰면 된다.

### 비한정적 와일드카드 타입
> 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 `물음표(?)를 사용하자`
> </br>어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 타입이다.
> 와일드카드 타입은 안전하고, 로 타입은 안전하지 않다.</br>
> 로 타입 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다.
> </br>반면, `Collection<?>`은 null 외에는 어떤 원소도 넣을 수 없다.</br>

### 로 타입을 써야하는 경우
> `class 리터럴`
> </br>ex) List.class, String[].class, int.class
> </br>
> </br>
> `instaceof`
> </br>
> ex) if (o instanceof Set){ 
> </br>Set<?> s = (Set<?>) o;</br>
> }
> </br>어차피 와일드카드 써도 효능없다.

## 핵심 정리
> 로 타입을 사용하면 런타임에 예외가 일어날 수 있으니까 사용하지 마라.
> </br> 그저 제네릭이 도입되기 이전 코드와 호환성을 위해서 제공된 것이다.
> </br> `Set<Object>`는 어떤 타입의 객체도 저장할 수 있는 매개변수화 타입,
> </br> `Set<?>`는 모종의 타입 객체만 저장할 수 있는 와일드카드 타입,
> </br> `Set`은 제네릭 타입 시스템에 속하지 않는다.