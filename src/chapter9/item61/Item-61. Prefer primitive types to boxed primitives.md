박싱된 기본 타입보다는 기본 타입을 사용하라
## 기본 타입과 박싱 타입의 차이
1. `기본 타입`은 `값`만 갖고 있으나, `박싱 타입`은 `식별성(identity)`까지 가짐
	- 즉, `박싱 타입`의 두 인스턴스는 *값이 같아도* `서로 다르다고 식별`할 수 있음
2. `기본 타입` 값은 언제나 `유효`하나, `박싱 타입`은 `null 값`을 가질 수 있음
3. `기본 타입`이 `박싱 타입`보다 `시간 및 메모리 측면`에서 `더 효율적`임
## 발생 할 수 있는 문제
### 비교 연산 문제
```java
Comparator<Integer> naturalOrder =
	(i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);

naturalOrder.compare(new Integer(42), new Integer(42)); 
```
- 위와 같이 `Integer`를 오름차순으로 정렬하는 `비교자`가 있을때, `값이 같은 Integer`에 이 비교자를 사용하면 문제가 발생
	- 두 `Integer`의 값을 비교하기 위해 사용한 `==` 연산자가 두 참조의 `식별성`을 검사하게됨
	- 따라서, 두 인스턴스는 *값은 같지만* `다른 인스턴스`이므로, `==` 비교 연산의 결과가 `false`가 됨
	- 그 결과 예상한 반환 값인 0이 아닌, 1이 반환 됨
> 다만 문제에서 사용된 `Integer(int)` 생성자는 Java 9부터 `deprecated` 되었고, `Integer.valueOf(int)`를 사용할 경우 정상적으로 비교가 이루어짐
#### 대안
1. `Comparator`의 정적 메서드인 `naturalOrder()` 사용하기
2. `비교자`를 직접 만들거라면, `비교자 생성 메서드` 또는 *기본 타입을 받는* `정적 compare 메서드`를 사용할 것
```java
Comparator<Integer> naturalOrder = (iBoxed, jBoxed) -> {
	int i = iBoxed, j = jBoxed; // 지역 변수를 두어 오토박싱
	return i < j ? -1 : (i == j ? 0 : 1);
};
```
3. 기본타입 지역변수를 둔 뒤 해당 지역변수에 박싱타입 값을 할당하여 기본타입으로 저장 후 비교 수행
### 기본 타입과 박싱 타입의 혼용 문제
- `박싱 타입`과 `기본 타입`이 `혼용`된 연산을 수행하면, 박싱 타입의 `박싱이 자동으로 풀림`
- 이로 인해 두 가지 문제가 발생할 수 있음
```java
public class Unbelievable {
	static Integer i;
	public static void main(String[] args) {
		if (i == 42) // NullPointerException 발생!
			System.out.println("Unbelievable");
	}
}
```
- 만약 `null 값인 박싱 타입`의 *박싱을 풀 경우* `NPE`이 발생함
```java
public static void main(String[] args) {
	Long sum = 0L;
	for (long i = 0; i < Integer.MAX_VALUE; i++) {
		sum += i;
	} 
	System.out.println(sum);
}
```
- 또한, *박싱 타입과 기본 타입간의 연산*을 반복하면 `박싱 및 언박싱이 반복`되어 일어나 `성능이 느려짐`
## 박싱 타입을 써야 하는 적절한 경우
1. `매개변수화 타입(Parameterized Type)` 또는 `매개변수화 메서드(Parameterized Method)`의 `타입 매개변수`로 사용할 때
	- Java에서 `타입 매개변수`로 기본 타입을 지원하지 않기 때문에, `박싱 타입`을 사용해야 함
	- ex) `컬렉션`의 `원소`, `키`, `값`으로 사용
2. `리플렉션`을 통해 메서드를 호출할 때
	- ex) `Method.invoke(Object obj, Object... args)`와 같은 메서드에 *기본 타입 값을 넘겨도* 알아서 `오토 박싱`해줌
# 핵심 정리
- `기본 타입`, `박싱된 기본 타입` 중 하나를 써야한다면 가능한 `기본 타입`을 쓰는 것이 `간단하고 빠름`
- `두 박싱된 기본 타입`간 비교할 때, `==` 연산자는 원하지 않은 결과를 반환할 수 있음
- 같은 연산에서 `기본 타입`과 `박싱된 기본 타입`을 혼용할 경우 언박싱이 이뤄지는데 이때 `NPE`이 발생할 수 있음
- 또한, 기본 타입의 `박싱`은 `필요 없는 객체를 생성`하는 부작용을 낳을 수 있음