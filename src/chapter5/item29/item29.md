# ITEM29. 이왕이면 제네릭 타입으로 만들라
(StackArray 참고)
## 배열을 사용하는 코드를 제네릭으로 만들려 할 때 해결책
### 제네릭 배열 생성을 금지하는 제약을 대놓고 우회하기
- technic1.Stack
- 가독성이 더 좋다.
- 코드도 더 짧다.
- 형변환을 배열 생성 시 단 한 번만 하면 된다.
- 런타임 타입이랑 컴파일타임 타입이 다름 (E[] <-> Object[]) -> 힙 오염 가능
  - `elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];`
### elements 피드의 타입을 E[] -> Object[]로 변환
- technic2.Stack
- 형변환을 배열에서 원소 읽을 때(pop)할 때마다 해야 한다.
## 대다수의 제네릭 타입은 타입 매개변수에 대다수의 제약을 두지 않는다.
- `Stack<Object>`, `Stack<int[]>`, `Stack<List<String>>`, `Stack` 등 모두 허용
- 근데 primitive type은 허용하지 않는다.
  - `Stack<int>` -> `Stack<Integer>`로 바꿔야 한다.
  - 자바 제네릭 타입의 근본적인 문제
## 타입 매개변수에 제약을 두는 제네릭 타입이 있다.
- `java.util.concurrent.DelayQueue`
  - `DelayQueue<E extends Delayed>`
  - `Delayed`를 포함한 `Delayed`의 하위타입만 매개변수로 받겠다.
  - `DelayQueue`자신과 `DelayQueue`를 사용하는 클라이언트는 `DelayQueue`의 원소에서 형변환 없이 바로 `Delayed`의 메서드를 호출할 수 있다.
    (DelayQueueExample 참고)

## 정리
- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.
- 형변환 없이 사용할 수 있게 하려면 제네릭으로 해야할 경우가 많다.
- 기존 클라이언트에는 아무 영향을 주지 않으면서, 새로운 사용자를 편하게 해주는 길이다.