public 클래스에서는, public 필드가 아닌 접근자 메서드를 사용하라
## 퇴보한 클래스
```java
class Point {
	public double x;
	public double y;
}
```
- `인스턴스 필드들`을 `모아놓기만 하는 역할`을 하는 클래스를 말함
- 이러한 클래스의 `데이터 필드`들은 **직접 액세스**되므로, **`캡슐화`** 의 이점을 `제공하지 못함`
- 또한, *API를 수정하지 않고는* `내부 표현`의 `변경`, `불변성 적용`, *필드 접근시* `보조 작업`을 수행할 수 없음
### public class의 캡슐화
- 강경한 `객체 지향 프로그래머`들은 이러한 형태를 혐오하여, `private 필드`와 `public 접근자 메서드(Getter)`(*가변 클래스의 경우* `Setter`까지)를 갖춘 클래스로 대체하려고 함
```java
// 접근자와 변경자 메서드를 통해 데이터를 캡슐화하는 클래스
class Point {
	private double x;
	private double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() { return x; }
	public double getY() { return y; }

	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
}
```
- 이러한 구현은, `public 클래스`에서라면 *내부 표현을 변경 할 수 있는* `유연성`을 얻을 수 있고, *클라이언트가* `필드에 의존`하는 일을 `막을 수 있기` 때문에 올바른 방식임
### package-private/private 중첩 class의 캡슐화
- 하지만, `package-private` / `private 중첩` 클래스에서는 *클래스가 제공하고자 하는 추상 개념*만 올바르게 표현해준다면 `데이터 필드`를 `노출`해도 `문제되지 않음`
	- 이러한 방식은 `클래스 선언` 및 `이를 사용하는 클라이언트 측 코드` 모두에서 *접근자 메서드 방식보다* `가독성이 좋음`
	- 클라이언트 코드는 클래스의 `내부 표현에 의존`하지만 그 내부 표현이 `클래스가 포함된 패키지 안`에서만 동작하므로, 추후 *내부 표현을 변경해야할 때* `패키지 외부의 코드`는 `건드리지 않고 수정`할 수 있음
	- 특히, `private 중첩 클래스`의 경우 `변경 범위`가 *해당 클래스를* `둘러싸고 있는 클래스`로 더욱 `제한`됨
### public 클래스 필드를 노출한 Java 라이브러리의 사례
- `Java Platform Library`에도 `public 클래스`의 `필드`를 노출한 사례가 종종 있음
- 대표적인게 [java.awt.Point](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html)와  [java.awt.Dimension](https://docs.oracle.com/javase/8/docs/api/java/awt/Dimension.html)임
	- 특히 `Dimension`의 경우, 가변적으로 설계되어 있어 `Dimension`을 반환하는 `java.awt.Component.getSize()`의 경우 이를 호출하는 모든 곳에서 `방어적 복사`가 강제되는 성능적 문제 존재
### public 클래스 필드를 불변으로 노출한 경우
- `불변`일 경우 `직접 노출`시 *몇가지 단점은 줄어들지만*, *API 변경 없이는* `내부 표현의 변경이 힘들단 것`과 *필드 접근시* `보조 작업을 수행할 수 없다`는 점은 여전함
```java
public final class Time {
	private static final int HOURS_PER_DAY = 24;
	private static final int MINUTES_PER_HOUR = 60;
	
	public final int hour;
	public final int minute;
	
	public Time(int hour, int minute) {
		if (hour < 0 || hour >= HOURS_PER_DAY)
			throw new IllegalArgumentException("Hour: " + hour);
		if (minute < 0 || minute >= MINUTES_PER_HOUR)
			throw new IllegalArgumentException("Min: " + minute);
		this.hour = hour;
		this.minute = minute;
	}
}
```
# 핵심 정리
- `public 클래스`의 `가변 필드`는 절대 `직접 노출해서는 안 됨`
	- `불변 필드`일 경우, 그나마 낫지만 여전히 완벽하진 않음
- `package-private 클래스` 또는 `private 중첩 클래스`에서는 *불변/가변에 관계없이* 필드를 노출하는 것이 바람직한 경우도 있음
