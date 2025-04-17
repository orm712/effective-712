가능한 한 실패 원자적으로 만들어라
## 실패 원자적(failure-atomic)
- `호출된 메서드`가 `실패`하더라도, **해당 객체**는 메서드 `호출 전 상태를 유지`하는 특성
### 메서드를 실패 원자적으로 만드는 방법
#### 불변 객체로 설계하기
- 불변 객체는 *태생적으로* `실패 원자적`임
- 새로운 객체를 만드는 메서드를 호출하는 경우, 실패해 새로운 객체가 만들어질 수는 있으나 기존 객체가 불안정한 상태에 빠질 일은 없음
#### 작업 전 매개 변수의 유효성 검사하기 (아이템 49)
```java
public Object pop() {
	if (size == 0)
		throw new EmptyStackException();
	Object result = elements[--size];
	element[size] = null; // 다 쓴 참조를 해제
	return result;
}
```
- 객체의 내부 상태를 변경하기 전, 올바른 매개변수인지 검사하여 잠재적 예외의 가능성을 확인
- 위 `pop()`의 경우, if문 부분을 제외하더라도 스택이 비었을 경우 여전히 예외(`ArrayIndexOutOfBoundsException`)을 던지지만 해당 예외의 추상화 수준도 상황과 어울리지 않으며 size값이 음수가 되어 다음에 size를 사용하는 메서드를 호출할 경우 그 역시 실패하게 만듦
##### 실패 가능한 코드를 객체 상태 수정 코드 앞에 배치하기
```java
    private V put(K key, V value, boolean replaceOld) {
        Entry<K,V> t = root;
        if (t == null) {
            addEntryToEmptyMap(key, value);
            return null;
        }
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
		// 먼저 해당 Key가 원소가 Map의 어느 곳에 들어가야 할지 찾음
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else {
                    V oldValue = t.value;
                    if (replaceOld || oldValue == null) {
                        t.value = value;
                    }
                    return oldValue;
                }
            } while (t != null);
        } else {
			...
        }
		// 찾은 위치 다음에 Key-Value 엔트리를 추가
        addEntry(key, value, parent, cmp < 0);
        return null;
    }
```
- 출처: [openjdk github 소스코드 - TreeMap](jdk/src/java.base/share/classes/java/util/TreeMap.java at master · openjdk/jdk · GitHub)
- *계산 수행 전에는* `인수의 유효성`을 **검사할 수 없을때** 위 방식에 덧 붙여 사용할 수 있음
- 예를 들어, *어떤 정렬 기준*을 갖는 `Map`에 `원소를 추가하는 기능`을 만들어야 하는 경우 추가 이전 **Map에서 해당 원소가 들어갈 자리를 찾도록** 작성하는 것이 이러한 예시
	- 만약 잘못된 타입의 원소를 추가하려한 경우, 자리를 찾는 과정에서 `ClassCastException`이 발생
#### 객체의 복사본을 만들어 작업을 수행한 뒤 교체하기
- 데이터를 임시 자료구조에 저장한 뒤 작업하는게 더 빠를 경우 적용하기 좋음
- 예시
	```java
		public static List sort(List list) {  
		     Object[] arr = list.toArray();  
		     java.util.Arrays.sort(arr);  
		     return List.of(arr);  
		}
	```
	- `리스트의 정렬` 메서드에서, *정렬 수행 전* 리스트의 원소들을 `배열로 옮겨 담아 정렬`한 뒤 다시 `리스트로 반환`하도록 작성
		- `배열`의 경우 정렬 알고리즘 내부에서 `원소에 접근하는 속도가 훨씬 빠르기 때문`
	- 만약 정렬에 실패해도, `입력 리스트(list)`는 변하지 않음
#### 작업 중 발생한 실패를 가로채는 복구 코드를 통해 작업 전 상태로 되돌리기
- `내구성 있는(durable) 자료구조(디스크 기반)`에 쓰이는 방식으로, 일반적이진 않음
## 실패 원자성을 달성할 수 없는 경우
- 크게 두 가지 경우가 있으며, 이외에도 `실패 원자성 달성을 위한 비용/복잡도가 큰 연산`의 경우 굳이 **실패 원자적으로 구현하지 않아도 됨**
### 멀티 스레드 환경
- 여러 스레드들이 동기화 없이 동일한 객체를 동시에 수정한다면, `ConcurrentModificationException`와 같은 예외를 잡아도 객체가 여전히 쓸만한 상태인지는 알 수 없음
### 에러 
- `Error`의 경우 복구할 수 없기 때문에, 실패 원자적이게 만들 수 없음
# 핵심 정리
- 메서드 명세에 기술된 예외라도, 예외 발생시 객체의 상태는 메서드 호출 전/후 똑같이 유지되어야 함
- 이를 지킬 수 없다면 실패 시 객체 상태를 명세에 서술해야 함