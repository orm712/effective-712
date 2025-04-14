# 과도한 동기화는 피하라
과도한 동기화는 오히려 성능을 떨어트리고, 교착상태에 빠뜨린다.

> 재정의 가능한 메서드나 클라이언트가 넘겨준 함수 객체를 동기화 영역 안에서 호출하면 어떤 짓을 하는지 통제할 수 없기 때문에 예외를 일으키거나 교착상태에 빠지거나, 데이터를 훼손할 수 있다.
```java
    class ObservableSet<E> extends ForwardingSet<E> { // 래퍼 클래스
        public ObservableSet(Set<E> set) {
            super(set);
        }

        private final List<SetObserver<E>> observers
                = new ArrayList<>();

        public void addObserver(SetObserver<E> observer) {
            synchronized (observers) {
                observers.add(observer);
            }
        }

        public boolean removeObserver(SetObserver<E> observer) {
            synchronized (observers) {
                return observers.remove(observer);
            }
        }

        private void notifyElementAdded(E element) {
            synchronized (observers) {
                for (SetObserver<E> observer : observers)
                    observer.added(this, element);
            }
        }
        
        @Override public boolean add(E element) {
            boolean added = super.add(element);
            if (added)
                notifyElementAdded(element);
            return added;
        }

        @Override public boolean addAll(Collection<? extends E> c) {
            boolean result = false;
            for (E element : c)
                result |= add(element);  // notifyElementAdded를 호출한다.
            return result;
        }
    }

```

하지만, 이 코드는 외부에서 함수 객체(`SetObserver`)를 받아오기 때문에 아래와 같은 두가지 에러가 발생할 수 있는 위험에 노출되어 있다.

### ConcurrentModificationException 예외
한 스레드만 `observer` 리스트에 접근할 수 있기 때문에 동기화 관련 예외가 터지지 않을 것 같아 보인다.
```java
public static void main(String[] args) {
    ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());
    set.addObserver(new SetObserver<>() {
        public void added(ObservableSet<Integer> s, Integer e) {
            System.out.println(e);
            if (e == 23)
                s.removeObserver(this);
        }
    });

    for (int i = 0; i < 100; i++)
        set.add(i);
}
```

> - 0부터 23까지 출력한 후 자신을 remove 한 후에 종료할 것 같으나 실제로 실행해보면 0~23까지 출력한 후 예외가 발생한다. 
> - 이유는 added 메서드 호출이 일어난 시점이 notifyElementAdded가 Observer들의 리스트를 순회하는 도중이기 때문이다.

> - added 메서드에서 ObservableSet.removeObserver 메서드를 호출하고, 또 여기서 observers.remove 메서드를 호출하는데 여기서 문제가 발생한다. 
>   - 순회하고 있는 리스트에서 원소를 제거하려고 하는 것이다. 
> - notifyElementAdded 메서드에서 수행하는 순회는 동기화 블록 안에 있어 동시 수정이 일어나지 않지만 정작 자신이 콜백을 거쳐 되돌아와 수정하는 것을 막지 못한다.

### 실행자 서비스(ExecutorService)를 사용하여 다른 스레드가 Observer를 제거하도록 하는 예시
```java
public static void main(String[] args) {
    ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

    set.addObserver(new SetObserver<>() {
        public void added(ObservableSet<Integer> s, Integer e) {
            System.out.println(e);
            if (e == 23) {
                ExecutorService exec = Executors.newSingleThreadExecutor();
                try {
                    // 여기서 lock이 발생한다. (메인 스레드는 작업을 기리고 있음)
                    // 특정 태스크가 완료되기를 기다린다. (submit의 get 메서드)
                    exec.submit(() -> s.removeObserver(this)).get();
                } catch (ExecutionException | InterruptedException ex) {
                    throw new AssertionError(ex);
                } finally {
                    exec.shutdown();
                }
            }
        }
    })

    for (int i = 0; i < 100; i++) {
        set.add(i);
    }
}
```

> - 코드를 실행하면 예외는 발생하지 않지만 교착상태(Deadlock)에 빠진다. 
> - 백그라운드 스레드가 s.removeObserver 메서드를 호출하면 Observer를 잠그려 시도하지만 락을 얻을 수 없다.
>   - 메인 스레드가 이미 락을 잡고 있기 때문이다.

> - removeObserver 메서드에는 synchronized 키워드가 있기 때문에 실행 시 락이 걸린다. 
> - 동시에 메인 스레드는 백그라운 스레드가 Observer를 제거하기만 기다리는 중이다. 따라서 교착상태에 빠진다.

### 해결 방법
```java
private void notifyElementAdded(E element) {
    List<SetObserver<E>> snapshot = null;
    synchronized (observers) {
        snapshot = new ArrayList<>(observers);
    }
    for (SetObserver<E> observer : snapshot) {
        observer.added(this, element);
    }
}
```

> 동시성 컬렉션을 이용할 수도 있다. `synchronized` 키워드를 이용하지 않아도 된다.
```java
private final List<SetObserver<E>> observers = new CopyOnWriteArrayList<>();

public void addObserver(SetObserver<E> observer) {
    observers.add(observer);
}

public boolean removeObserver(SetObserver<E> observer) {
    return observers.remove(observer);
}

private void notifyElementAdded(E element) {
    for (SetObserver<E> observer : observers)
        observer.added(this, element);
}
```

> - CopyOnWriteArrayList는 ArrayList를 구현한 클래스로 내부를 변경하는 작업은 항상 깨끗한 복사본을 만들어 수행하도록 구현돼있다. 
> - 내부의 배열은 수정되지 않아 순회할 때 락이 필요 없어 매우 빠르다. 
> - 다른 용도로 사용된다면 매번 복사해서 느리지만, 수정할 일이 적고 순회만 빈번하게 일어난다면 Observer 리스트 용도로는 최적이다.

### 동기화 영역에서 작업 최소화
> - 과도한 동기화는 병렬로 실행할 기회를 잃고, 모든 코어가 메모리를 일관되게 보기 위한 지연 시간이 진짜 비용이다.
>   - 동기화 영역 밖으로 옮기는 방법을 찾아보는 것도 좋다.