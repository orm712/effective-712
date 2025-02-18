가변인수는 신중히 사용하라
## 가변인수 메서드 
```java
static int sum(int... args) {
	int sum = 0;
	for (int arg : args)
		sum += arg;
	return sum;
}
```
- [JLS, 8.4.1](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4.1)
- [Java 5에 공개된 기능](https://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html)으로, `명시한 타입의 인수`를 `0개 이상` 받을 수 있는 메서드
- 메서드가 호출되면, `인수의 개수`와 `길이가 같은 배열`을 만들고, 인수들을 `배열에 저장`해 가변인수 메서드에게 건네주게 됨
	- 따라서, `인수의 개수`는 *런타임에* `배열 길이`를 측정하면 알 수 있음
- `printf`와 `리플렉션`(ex. `Method.invoke(Object obj, Object... args)`)이 가변인수의 효과를 많이 본 예시
### 매개변수를 1개 이상 받아야 할 경우
```java
static int min(int firstArg, int... remainingArgs) {
	int min = firstArg;
	for (int arg : remainingArgs)
		if (arg < min)
			min = arg;
	return min;
}
```
- 만약 매개변수를 `1개 이상` 받음을 강제하려면, 매개변수를 `2개` 받도록 하면 코드가 명료해짐
- *기존처럼 가변인수 하나만 받았다면*, 배열 길이를 측정하고 0이면 예외를 던지게 하는 등 코드가 지저분했을 것임
### 성능적 문제
- *가변인수를 사용할 경우*, 위에서 설명한 것처럼 메서드가 *호출될 때마다* 배열을 할당하고 초기화하게 됨
	- 이는 성능에 민감한 경우 문제가 될 수 있음
```java
public void foo() { }
public void foo(int a1) { }
public void foo(int a1, int a2) { }
public void foo(int a1, int a2, int a3) { }
// 4개 이상의 인수는 아래 메서드에서 처리 됨
public void foo(int a1, int a2, int a3, int... rest) { }
```
- 이럴 경우, 많이 사용되는 *인수의 가짓 수 만큼* `다중정의`를 하면 됨
	- 만약 인수를 3개 이하로 사용하는 경우가 대부분이라면, 0개, 1개, 2개, 3개를 받는 메서드를 두고 그 이상은 가변인수로 받는 메서드를 하나 더 두면 되는 것
```java
static <E extends Enum<E>> EnumSet<E>	of(E e)
static <E extends Enum<E>> EnumSet<E>	of(E first, E... rest)
static <E extends Enum<E>> EnumSet<E>	of(E e1, E e2)
static <E extends Enum<E>> EnumSet<E>	of(E e1, E e2, E e3)
static <E extends Enum<E>> EnumSet<E>	of(E e1, E e2, E e3, E e4)
static <E extends Enum<E>> EnumSet<E>	of(E e1, E e2, E e3, E e4, E e5)
```
- `EnumSet`의 `정적 팩터리` 역시 위 기법을 사용하여 열거 타입 집합의 생성 비용을 최소화하고 있음 [#](https://docs.oracle.com/javase/8/docs/api/java/util/EnumSet.html)
# 핵심 정리
- 인수 개수가 일정하지 않은 메서드 정의를 위해서는 가변인수가 필수적임
- 메서드 정의시 `필수 매개변수`는 `가변인수 앞`에 두고, 가변인수 사용시 성능 문제까지 고려할 것