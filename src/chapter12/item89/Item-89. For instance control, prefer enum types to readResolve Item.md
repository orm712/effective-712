인스턴스 수를 통제해야 한다면, `readResolve`보다는 열거 타입을 사용하라
## 싱글턴의 역직렬화 문제
```java
public class Elvis {
	public static final Elvis INSTANCE = new Elvis(); // 클래스 로딩과 함께 초기화
	private Elvis() { ... } // 클래스 바깥에서의 생성자 호출 방지
	
	public void leaveTheBuilding() { ... }
}
```
- [아이템 3](https://github.com/orm712/effective-712/blob/main/src/chapter1/item3/item-3.%20Enforce%20the%20singleton%20property%20with%20a%20private%20constructor%20or%20an%20enum%20type.md)에서 말했듯, 위 클래스의 선언에 `implements Serializable`를 추가하는 순간 싱글턴은 지켜지지 않음 (*직렬화 및 역직렬화를 거치면* `INSTANCE`와 **`구분되는 새 인스턴스`** 가 만들어지기 때문)
	- 이는 *기본 직렬화를 쓰지 않아도*(아이템 87), *명시적인 `readObject`를 제공해도*(아이템 88) **`동일함`**
	- 어떤 `readObject`를 쓰든, *이 클래스가 초기화 될 때 만들어진 인스턴스*와 `별개인 인스턴스`를 반환하게 됨
- 만약, [`readResolve`](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/input.html#a5903)를 사용한다면 `readObject`가 만들어낸 인스턴스를 대체할 수 있음
	- `readResolve`는 `ObjectInputStream`이 스트림에서 객체를 읽은 뒤, *호출자에게 `역직렬화한 인스턴스가 아닌 다른 값으로 대체`* 할 수 있도록 해주는 메서드 [참고](https://github.com/orm712/effective-712/blob/main/src/chapter1/item3/readResolveTest.java)
```java
private Object readResolve() {
	// 진짜 Elvis (INSTANCE) 를 반환하고, 가짜는 GC에게 맡긴다.
	return INSTANCE;
}
```
- 이를 반영해 위 코드를 개선한다면, 다음과 같이 `readResolve` 메서드를 추가할 수 있음
	- 이렇게 할 경우, 역직렬화한 객체는 무시되므로 Elvis 인스턴스의 `직렬화 형태`는 아무런 **`실제 값을 가질 이유가 없음`**
		- 따라서, `모든 인스턴스 필드`를 `transient`(해당 키워드가 선언된 필드는 직렬화 수행 X)로 선언해야 함
		- 특히, `readResolve`를 **인스턴스 통제 목적**으로 사용할 경우, `객체 참조 타입의 인스턴스 필드`는 모두 `transient`로 설정해야 함
			- 이렇게 하지 않으면 `MutablePeriod`와 **유사한 공격방식**으로 `readResolve` 수행 전 `역직렬화된 객체의 참조`를 `공격할 여지`가 생김 (*정상 인스턴스의* `바이트 스트림 끝`에 `내부 private 객체 필드로의 참조 추가`시 이들을 수정할 수 있는 공격)
#### readResolve 공격 취약점
```java
// 싱글턴 클래스
public class Elvis implements Serializable {
	public static final Elvis INSTANCE = new Elvis();
	private Elvis() { }

	// transient가 아닌 참조 필드를 갖고 있음
	private String[] favoriteSongs =
		{ "Hound Dog", "Heartbreak Hotel" };
	public void printFavorites() {
		System.out.println(Arrays.toString(favoriteSongs));
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}

// 도둑 클래스
public class ElvisStealer implements Serializable {
	static Elvis impersonator;
	private Elvis payload;
	
	private Object readResolve() {
		// resolve 되기 전의 Elvis 인스턴스의 참조를 저장
		impersonator = payload;
		// favoriteSongs 필드에 맞는 타입의 객체를 반환함
		return new String[] { "A Fool Such as I" };
	}
	private static final long serialVersionUID = 0;
}

// 메인 문이 실행될 클래스
public class ElvisImpersonator {
	// 진짜 Elvis 인스턴스로는 만들어질 수 없는 임의의 바이트 스트림
	private static final byte[] serializedForm = {
		(byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x05,
		0x45, 0x6c, 0x76, 0x69, 0x73, (byte)0x84, (byte)0xe6,
		(byte)0x93, 0x33, (byte)0xc3, (byte)0xf4, (byte)0x8b,
		0x32, 0x02, 0x00, 0x01, 0x4c, 0x00, 0x0d, 0x66, 0x61, 0x76,
		0x6f, 0x72, 0x69, 0x74, 0x65, 0x53, 0x6f, 0x6e, 0x67, 0x73,
		0x74, 0x00, 0x12, 0x4c, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c,
		0x61, 0x6e, 0x67, 0x2f, 0x4f, 0x62, 0x6a, 0x65, 0x63, 0x74,
		0x3b, 0x78, 0x70, 0x73, 0x72, 0x00, 0x0c, 0x45, 0x6c, 0x76,
		0x69, 0x73, 0x53, 0x74, 0x65, 0x61, 0x6c, 0x65, 0x72, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,
		0x4c, 0x00, 0x07, 0x70, 0x61, 0x79, 0x6c, 0x6f, 0x61, 0x64,
		0x74, 0x00, 0x07, 0x4c, 0x45, 0x6c, 0x76, 0x69, 0x73, 0x3b,
		0x78, 0x70, 0x71, 0x00, 0x7e, 0x00, 0x02
	};
	private static Object deserialize(byte[] sf) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(sf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
	public static void main(String[] args) {
	// ElvisStealer.impersonator를 초기화 한 뒤,  
	// 진짜 Elvis(즉, `Elvis.INSTANCE`)를 반환  
	  
	// 1. Elvis deserialize -> Elvis에 포함된 ElvisStealer의 역직렬화 먼저 진행  
	// 2. ElvisStealer의 readResolve가 호출되며, 부분적으로 역직렬화되고 readResolve 호출 전인 Elvis가  
	// ElvisStealer.payload로 연결됨  
	// 3. 그리고 `impersonator = payload` 라인이 호출되면서, 정적 필드인 impersonator로 복사됨  
	// 이를 통해 readResolve가 끝난 이후에도 해당 인스턴스를 참조할 수 있음  
	// 따라서, 원래 싱글턴이였어야 할 Elvis는  
	// 1. 역직렬화된 Elvis,// 2. 역직렬화 과정 중간에 낚아챈 Elvis (ElvisStealer.impersonator)// 두 개의 인스턴스가 만들어져버림
		Elvis elvis = (Elvis) deserialize(serializedForm);
		Elvis impersonator = ElvisStealer.impersonator;
		elvis.printFavorites();
		impersonator.printFavorites();
	}
}
```
- 이 공격을 위해서는 "`readResolve` 메서드와 `인스턴스 필드` 하나"를 포함하는 '도둑' 클래스를 작성해야 함
	- 해당 클래스가 가진 `인스턴스 필드`는 도둑이 '감출', `직렬화된 싱글턴`을 `참조`하는 역할
- 그리고 `직렬화된 스트림`에서 싱글턴의 `비휘발성 필드`(`favoriteSongs`)를 이 `'도둑' 클래스 인스턴스`로 **교체**하게되면, `싱글턴`은 `도둑`을 **`포함`** 하고, `도둑`은 `싱글턴`을 **`참조`** 하는 **`순환 참조`** 가 만들어짐
	- `싱글턴`이 '도둑'을 포함하고 있기 때문에, `싱글턴`이 `역직렬화`될 때 '도둑'의  `readResolve` 메서드가 먼저 호출되게 되고, 따라서 '도둑'의 `readResolve`가 호출되는 시점에서 '도둑' 의 `인스턴스 필드`에는 *부분적으로 역직렬화되고 readResolve 호출 전인* `싱글턴`의 참조가 담김
- '도둑'의 `readResolve` 메서드는 해당 `인스턴스 필드`가 참조한 값을 **`정적 필드로 복사`** 해, `readResolve`가 끝난 이후에도 참조 할 수 있도록 함
	- 이후 이 메서드는, 필드의 `원래 타입에 맞는 값`을 반환하도록 함(`return new  String[] ...`)
	- 만약 *이 과정을 생략할 경우*, 직렬화 시스템이 '도둑'의 참조를 이 필드에 저장하려 할 때 VM이 `ClassCastException`을 발생시킬 것
- 이러한 공격은 `비휘발성 필드`를 `transient`로 선언해 고칠 수 있지만, `Elvis`를 원소 하나짜리 **`열거 타입`** 으로 바꾸는 편이 더 나음
	- 다만, `직렬화 가능 인스턴스 통제 클래스`를 작성할 때 *컴파일 시간에* **어떤 인스턴스들이 있는지 알 수 없는 경우**에는 `readResolve`를 사용해야 함
## 열거 타입의 장점
```java
public enum Elvis {
	INSTANCE;
	private String[] favoriteSongs =
		{ "Hound Dog", "Heartbreak Hotel" };
	public void printFavorites() {
		System.out.println(Arrays.toString(favoriteSongs));
	}
}
```
- `직렬화 가능한 인스턴스 통제 클래스`를 `열거 타입`으로 구현하면, *선언한 상수 외의 `다른 객체`* 는 **`존재하지 않음`** 을 **자바가 보장**해줌
	- 다만, `AccessibleObject.setAccessible`(Reflection)과 같이 `특권(Privileged)` 메서드를 악용할 경우 이러한 **방어는 무력화** 됨
## readResolve 메서드의 접근성
- `readResolve` 메서드의 접근 제어자와 관련해 몇 가지 고려해야 할 사항이 있음
- `final` 클래스라면 `readResolve` 메서드는 private여야 함
- `final` 클래스가 아니라면, 다음과 같은 사항을 고려해야 함
	- `protected` 또는 `public` 이면서, *하위 클래스에서* **재정의하지 않은 경우** 하위 클래스의 인스턴스를 역직렬화하면 `상위 클래스 인스턴스`를 **생성**해 `ClassCastException`이 발생할 수 있음
# 핵심 정리
- *불변식을 지키기 위해* 인스턴스를 통제해야 한다면, 가능한 `열거 타입`을 쓸 것
	- 불가능할 경우, `직렬화`와 `인스턴스 통제` 모두 필요하다면 `readResolve` 메서드를 작성해야 하고, 해당 클래스 내 **모든** `참조 타입 인스턴스 필드`를 `transient`로 선언해야 함