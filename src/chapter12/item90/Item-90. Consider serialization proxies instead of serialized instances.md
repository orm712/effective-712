직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라
## 직렬화 프록시 패턴
- 앞선 아이템들에서 볼 수 있듯, `Serializable`을 `implements` 하는 순간 *생성자 이외의* `인스턴스 생성 방법`이 열리게 되고 이는 **버그 및 보안 문제**로 직결됨
- 하지만, **`직렬화 프록시 패턴(Serialization Proxy Pattern)`** 을 사용하면 이러한 위험을 크게 줄일 수 있음
### 직렬화 프록시 패턴을 사용하는 방법
- *바깥 클래스의 논리적 상태*를 **간결하게 표현**하는 `중첩 클래스`를 `private static`으로 선언
	- 이 `중첩 클래스`가 바로 *바깥 클래스의* **`직렬화 프록시`**
	- `중첩 클래스`의 `생성자`는 **`단 하나`** 여야 하며, `바깥 클래스`를 `매개변수`로 받음
		- 생성자는 *인자로 넘어온* `인스턴스의 데이터`를 `복사`만 함
- 또한, `바깥 클래스`, `직렬화 프록시` 모두 `Serializable`을 `implements`해야 함
### 예시 - Period의 직렬화 프록시
```java
// Period 클래스를 위한 직렬화 프록시
private static class SerializationProxy implements Serializable {
	// 바깥 클래스의 상태를 표현하는 변수들
	// 대상이 되는 바깥 클래스인 Period의 상태가 간단하므로
	// 직렬화 프록시도 완전히 같은 필드로 구성됨
	private final Date start;
	private final Date end;
	
	SerializationProxy(Period p) {
		this.start = p.start;
		this.end = p.end;
	}
	private static final long serialVersionUID =
		234098243823485285L; // 아무 값이나 임의로 쓴 것
}

// 바깥 클래스에 추가할 writeReplace
private Object writeReplace() {
	return new SerializationProxy(this);
}
```
- 위 코드에서 `writeReplace`는 자바의 `직렬화 시스템`이 *`바깥 클래스의 인스턴스` 대신* `SerializationProxy의 인스턴스를 반환`하도록 하는 역할
	- 즉, *직렬화가 이뤄지기 전* `바깥 클래스의 인스턴스`를 `직렬화 프록시`로 변환해줌
- 이 `writeReplace` 때문에 직렬화 시스템은 `바깥 클래스의 직렬화된 인스턴스`를 절대 생성할 수 없음
	```java
	private void readObject(ObjectInputStream stream)
			throws InvalidObjectException {
		throw new InvalidObjectException("Proxy required");
	}
	```
	- 다만, 공격자가 불변식을 훼손하기 위해 *인스턴스를 조작하려 할 수 있으므로* 바깥 클래스에 위 `readObject` 메서드를 추가하면 막을 수 있음
```java
private Object readResolve() {
	return new Period(start, end); // public 생성자 사용
}
```
- 그리고, `바깥 클래스`와 **`논리적으로 동일`** 한 인스턴스를 반환하는 `readResolve` 메서드를 `SerializationProxy` 클래스에 추가해야 함
	- 이는 `역직렬화`시 직렬화 시스템이 `직렬화 프록시`를 `바깥 클래스의 인스턴스`로 다시 변환함
	- `readResolve`는 *`공개된 API(위 코드에서는 공개 생성자)`만을 사용해* `바깥 클래스의 인스턴스를 생성`하므로, **일반 인스턴스를 만들때와 똑같은** `생성자`, `정적 팩터리` 등의 메서드를 사용해 `역직렬화된 인스턴스`를 **생성**함
		- 이는 기존의 `직렬화`가 제공하는 **`생성자 없이` 인스턴스를 생성** 하는 기능을 사용했을 때 발생하는 `변종, 공격들을 검사할 수고`를 **줄여줌**
		- 애초에 `readResolve`에서 사용한 `생성자`나 `정적 팩터리` 메서드가 불변식을 검사하고, `인스턴스 메서드`들이 불변식을 잘 지켜준다면 *직렬화를 거쳐도* **불변식이 유지**됨
### 직렬화 프록시 패턴의 이점
 1. *`방어적 복사`처럼*, `가짜 바이트 스트림 공격`(코드 88-2, BogusPeriod)과 `내부 필드 탈취 공격`(코드 88-4, MutablePeriod)을 `프록시 수준`에서 **`차단`** 해줌
 2. 또한, `직렬화 프록시`는 `Period`의 필드를 `final`로 선언해도 사용 가능하므로, 바깥 클래스(`Period`)를 **`불변 클래스`** 로 만들 수 있음
 3. 그리고 앞서 말했듯, `직렬화 공격에 대한 고려`과 `역직렬화시 유효성 검사`를 수행할 수고를 덜 수 있음
 4. `직렬화 프록시` 패턴은 `역직렬화한 인스턴스`와 원래의 `직렬화된 인스턴스`의 **`클래스가 달라도`** **정상 동작**함
	 - 이는 `EnumSet`과 같이, *열거 타입의 원소 개수*에 따라 사용하는 `인스턴스 타입이 다른 경우` 유용함
		```java
		// EnumSet의 정적 팩터리 메서드 중 하나인 noneOf
		public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
			Enum[] universe = getUniverse(elementType);
			if (universe == null)
				throw new ClassCastException(elementType + " not an enum");
	
			if (universe.length <= 64)
				return new RegularEnumSet<>(elementType, universe);
			else
				return new JumboEnumSet<>(elementType, universe);
		}
		```
		 - `EnumSet`은 주어진 `열거 타입`을 기반으로, 열거 타입의 *원소가 64개 이하라면* `RegularEnumSet`을, 초과라면 `JumboEnumSet`을 반환하는 `정적 팩터리`들만 사용함
		 - 만약 **원소 64개짜리 열거 타입**을 갖는 `EnumSet`을 `직렬화` 한 뒤, **원소 5개를 추가**하고 `역직렬화`를 한다면, 직렬화 된 것은 `RegularEnumSet`, 역직렬화 된 것은 `JumboEnumSet` 인스턴스면 좋을 것임
		 - 이를 `직렬화 프록시`로 구현하면 아래와 같음
```java
private static class SerializationProxy <E extends Enum<E>>
	implements Serializable {
	// 이 EnumSet의 원소 타입
	private final Class<E> elementType;
	
	// 이 EnumSet 안의 원소들
	private final Enum<?>[] elements;
	
	SerializationProxy(EnumSet<E> set) {
		elementType = set.elementType;
		elements = set.toArray(new Enum<?>[0]);
	}
	
	private Object readResolve() {
		// 원소 타입 기반으로, 새 EnumSet을 생성하는 정적 팩터리 메서드
		EnumSet<E> result = EnumSet.noneOf(elementType);
		for (Enum<?> e : elements)
			result.add((E)e);
		return result;
	}
	private static final long serialVersionUID =
	362491234563181265L;
}
```
### 직렬화 프록시 패턴의 한계
1. 클라이언트가 멋대로 **`확장할 수 있는 클래스`** 에는 **적용할 수 없음**
2. `객체 그래프`에 **순환이 있는** 클래스에는 **적용할 수 없음**
	- 이런 객체의 메서드를 `직렬화 프록시`의 `readResolve` 안에서 호출할 경우, `ClassCastException`이 발생할 것인데, `직렬화 프록시`만 있지 `실제 객체`가 **아직 만들어진 것이 아니기 때문**
3. *`방어적 복사`와 같은 방법보다* 성능이 낮음
	- 저자의 경우 14% 정도 더 느렸다고 함
# 핵심 정리
- 제3자가 확장할 수 없는 클래스라면 가능한 `직렬화 프록시` 패턴을 사용하자
	- 이는 `중요한 불변식`을 안정적으로 직렬화해주는 가장 쉬운 방법일 것임