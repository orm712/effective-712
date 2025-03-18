리플렉션보다는 인터페이스를 사용하라
## 리플렉션
- [java.lang.reflect](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/package-summary.html)에 있는 리플렉션 기능을 사용하면 *클래스, 메서드, 필드*의 `정보 접근 및 조작`이 가능하며 `런타임 시점에 새롭게 등장한 클래스`도 `이용`할 수 있음
### 리플렉션의 단점
- 그럼에도 불구하고 `코드 분석 도구`, `의존관계를 주입하는 프레임워크` 등 리플렉션을 사용하는 애플리케이션들도 다음과 같은 단점 때문에 `리플렉션 사용을 점차 줄이고 있음`
#### 컴파일타임 검사의 이점을 포기해야함
- 컴파일 시간에 이뤄지는 타입 및 예외 검사의 이점을 누릴 수 없음
	- 즉, 리플렉션으로 인한 오류는 `런타임 시간 오류`임
#### 코드의 가독성이 떨어짐
#### 성능이 떨어짐
- 리플렉션을 통한 메서드 호출은 일반 메서드 호출보다 훨씬 느림
### 리플렉션의 권장 사용 방법
```java
public static void main(String[] args) {
	// 클래스 이름을 기반으로 Class 객체 획득
	Class<? extends Set<String>> cl = null;
	try {
		cl = (Class<? extends Set<String>>) // 비검사 형변환(uncheked cast). 이로 인해 컴파일 시 비검사 형변환 경고가 표시됨
			Class.forName(args[0]);
	} catch (ClassNotFoundException e) {
		fatalError("Class not found.");
	}
	
	// 생성자 획득
	Constructor<? extends Set<String>> cons = null;
	try {
		cons = cl.getDeclaredConstructor();
	} catch (NoSuchMethodException e) {
		fatalError("No parameterless constructor");
	}
	// set 인스턴트 생성
	Set<String> s = null;
	try {
		s = cons.newInstance();
	} catch (IllegalAccessException e) {
		fatalError("Constructor not accessible");
	} catch (InstantiationException e) {
		fatalError("Class not instantiable.");
	} catch (InvocationTargetException e) {
		fatalError("Constructor threw " + e.getCause());
	} catch (ClassCastException e) {
		fatalError("Class doesn't implement Set");
	}
	
	// set 사용
	// 커맨드라인 인수의 두 번째 인수부터 마지막 인수까지를 Set에 추가한 뒤 출력
	// 이때, 출력되는 순서는 s의 구현체 별 정렬 기준에 따라 바뀜
	s.addAll(Arrays.asList(args).subList(1, args.length));
	System.out.println(s);
}
	
private static void fatalError(String msg) {
	System.err.println(msg);
	System.exit(1);
}
```
- `컴파일시간에 쓸 수 없는 클래스`의 인스턴스를 생성할 때만 사용하는 것을 권장
	- 이때, `해당 클래스`는 접근 가능한 *(해당 클래스가 구현한)*`인터페이스` 또는 `상위 클래스`를 통해 `참조`함
	- 위 코드에서는 커맨드라인의 `첫 번째 인수(args[0])`로 주어진 `클래스`를 생성하는데, 해당 클래스는 `Set<String>`을 구현한 클래스여야 함
- 다만, 이렇게 사용해도 리플렉션의 단점에 영향을 받을 수 밖에 없음
	- 리플렉션으로 인해 *런타임에 6개의 예외가 발생할 수 있음*
		- 리플렉션을 쓰지 않았다면 모두 `컴파일 시간`에 검사할 수 있었음
	- 클래스 이름만으로 인스턴스를 생성하기 위해 *장황한 코드를 작성함*
		- 이로 인해 `코드 가독성이 떨어짐`
		- 다만, 예외들을 따로따로 잡지 않고 [ReflectiveOperationException](https://docs.oracle.com/javase/8/docs/api/java/lang/ReflectiveOperationException.html)(Java 7부터 지원) 이라는 예외 `하나로 묶어` 잡을 수도 있음
	- *이러한 단점들*은 `객체를 생성할 때`만 발생하며, 생성 이후로는 평소 `Set` 인스턴스를 사용하던 것과 동일함
### 또 다른 용도 - 의존성 관리
```java
    public static void main(String[] args) {
		// 객체 생성
		// 예시에서는 가상의 Set 구현체인 SuperSet을 사용한다고 가정
		Class<?> superSetClass = Class.forName("com.example.collection.SuperSet"); 
		tempInstance = superSetClass.getDeclaredConstructor().newInstance();
		
		// Set에 초기 값 추가
		Method addAllMethod = superSetClass.getMethod("addAll", java.util.Collection.class);
        addAllMethod.invoke(tempInstance, internalSet);
            
        
		// 최신 기능(FrequencyMethod)을 불러오고 사용
		try {
			
			// 최신 기능 확인: getFrequency 메서드 (해시 충돌 빈도 확인)
			Method getFrequencyMethod = superSetClass.getMethod("getFrequency", Object.class);
			getFrequencyMethod.invoke(tmepInstance, ...);
			
		} catch (NoSuchMethodException e) {
			// 해당 기능이 없는 버전을 사용하는 경우, 기능을 축소하여 동작
			...
		}
    }
}
```
- 드물게 `리플렉션`을 *런타임 시간에 "없을 수 있는"* 다른 `클래스`/`메서드`/`필드`와의 `의존성을 관리`할 때 쓰기도 함
	- 가령, `버전이 여러 개 존재`하는 `외부 패키지`를 사용하는 코드를 작성해야하는 경우, 해당 패키지를 지원하는데 필요한 `최소한의 환경`(*일반적으로* `가장 오래된 버전`)을 기준으로 `컴파일`한 뒤 *이후 버전*은 `리플렉션으로 접근`
	- 이러한 방식으로 사용할 경우, *액세스하려는* `최신 클래스` 또는 `메서드`가 `런타임에 존재하지 않을 경우 수행할 동작`도 설정해주어야 함
		- 예를 들어, 대체 수단을 이용하도록 하거나, 기능을 축소해 동작한다거나 하는 식으로 보완책을 설정해야 함
# 핵심 정리
- `리플렉션`은 복잡한 특수 시스템을 개발할때 필요한 강력한 기능이나, 단점도 많음
	- `컴파일 시간에 알 수 없는 클래스`를 써야할 경우 리플렉션을 사용해야 함
- `리플렉션`은 되도록 객체 생성에만, *생성한 객체*는 *컴파일시간에 알 수 있는* `인터페이스` 또는 `상위 클래스`에 할당해서 쓸 것