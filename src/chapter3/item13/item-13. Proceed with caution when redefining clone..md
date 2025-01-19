# Clone 재정의는 주의해서 진행하라

## Clonable 인터페이스
`Cloneable`은 어떤 객체가 복제(clone)을 허용한다는 사실을 알리기 위해 만들어진 믹스인 인터페이스이다.</br>
인터페이스 내부를 살펴보면 clone 메서드가 선언되어 있지 않고, `Object 클래스`에 clone 메서드가 protected로 선언되어 있다.</br>
Cloneable 인터페이스는 Object 클래스의 clone 메서드가 어떻게 동작할 것인지에 대해 결정한다. </br>
Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면, 해당 객체를 필드 단위로 복사한 객체를 반환한다. </br>
반대로 Cloneable을 구현하지 않으면, `CloneNotSupportedException` 예외를 던진다.

## Cloneable 인터페이스 구현하기
```java
public class User implements Cloneable {

    private String name;
    
    public User(String name) {
        this.name = name;
        }
    
    public String getName() {
        return name;
        }
    
    public void setName(String name) {
        this.name = name;
        }
    
    @Override
    public User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }
}
```

Object 클래스의 clone 메서드의 접근제한자는 protected이기 때문에 public으로 재정의해야 한다.</br>
그렇지 않으면 다른 클래스에서 clone 메서드를 호출할 수 없다. </br>

User 클래스의 객체를 생성하고, 인스턴스를 통해 clone 메서드를 호출해보자. </br>
정상적으로 복제가 되었다면 두 개의 객체는 같은 값을 가져야 한다.
```java
User user = new User("jayden-lee");
User user2 = (User) user.clone();
Assert.assertEquals(user.getName(), user2.getName()); //true가 되어야 함
```

## 가변 객체가 포함된 객체 복사는 조심해야 한다.
> 가변 객체가 포함된 클래스를 복사하는 경우에는 super.clone() 결과에서 예상치 못하게 오류가 발생할 수 있다.

User 클래스에 List 필드를 추가하고, 복제된 User 인스턴스에서 가변 객체의 값을 변경하면, 원본 객체도 동일하게 변경된다. </br>
`Asser.assertEquals(1, user.getFriendNames().size())`에서 기대한 값과 다른 값이 반환되어 실패한다.
```java
User user =new User("jayden-lee");
user.addFriendName("ella");

User user2 = (User) user.clone();
user2.addFriendName("wedul");

Assert.assertEquals(1, user.getFriendNames().size());  //둘 다 2이므로 실패
Assert.assertEquals(2, user2.getFriendNames().size());
```
원본과 복제본 객체는 동일한 가변 객체를 참조하고 있기 때문에 어느 한 곳에서 수정이 일어나면 다른 곳도 영향을 받게 된다. </br>
이처럼 가변 객체가 포함되어 있는 객체를 복사시에는 조심해야 한다. </br>

### 가변 객체가 포함되어 있는 객체의 clone - Stack 클래스
```java
public class Stack {
	private Object[] elements;
	private int size = 0;
	...
}
```

### Stack 클래스를 clone 하면 어떻게 될까?
반환된 Stack 인스턴스의 size 필드는 올바른 값을 갖겠지만, elements 필드는 원본 Stack 인스턴스와 똑같은 배열을 참조할 것이다. </br>
원본이나 복제본 중 하나를 수정하면 다른 하나도 수정되어 불변식을 해친다는 이야기다.

### clone 메서드는 사실상 생성자와 같은 효과를 낸다.
clone은 원본 객체에 아무런 해를 끼치지 않는 동시에 복제된 객체의 불변식을 보장해야 한다. </br>
이를 해결하기 가장 쉬운 방법은 elements 배열의 clone을 재귀적으로 호출해주는 것이다.
```java
@Override
public Stack clone() {
	try {
    	Stack result = (Stack) super.clone();
		result.elements = elements.clone();
		return result;
	} catch (CloneNotSupportedException e) {
		throw new AssertionError();
	}
}
```

그런데 clone을 재귀적으로 호출하는 것만으로는 충분하지 않을 때도 있다. </br>
이번에는 해시테이블용 clone 메서드를 생각해보자.</br>
해시테이블 내부는 버킷들의 배열이고, 각 버킷은 key-value 쌍을 담는 연결리스트의 첫 번째 엔트리를 참조한다.

```java
public class HashTable implements Cloneable {
    private Entry[] buckets = ...;

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        ... // 나머지 코드는 생략
    }

    @Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = buckets.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```
복제본은 자신만의 버킷 배열을 갖지만, 이 배열은 원본과 같은 연결 리스트를 참조하여 원본과 복제본 모두 예기치 않게 동작할 가능성이 생긴다. </br>
이를 해결하려면 각 버킷을 구성하는 연결 리스트를 복사해야 한다.

```java
public class HashTable implements Cloneable {
	...

	Entry deepCopy() {
    	return new Entry(key, value, next == null ? null : next.deepCopy());
    }

	@Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++)
                if (buckets[i] != null)
                    result.buckets[i] = buckets[i].deepCopy();
                return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
	}
    
}
```
이때 Entry의 deepCopy 메서드는 자신이 가리키는 연결 리스트 전체를 복사하기 위해 자신을 재귀적으로 호출한다.

Cloneable을 구현하는 모든 클래스는 clone을 재정의해야 한다. </br>
이때 접근 제한자는 public으로 반환 타입은 클래스 자신으로 변경한다. </br>
이 메서드는 가장 먼저 super.clone()을 호출한 후, 필요한 필드를 전부 적절히 수정한다.

기본 타입 필드와 불변 객체 참조만 갖는 클래스라면 아무 필드도 수정할 필요 없다. </br>
단, 일련번호나 고유 ID는 비록 기본 타입이나 불변일지라도 수정해줘야 한다.

### 그런데 이 모든 작업이 꼭 필요한 걸까?
다행히도 이처럼 복잡한 경우는 드물다. </br>
Cloneable을 이미 구현한 클래스를 확장한다면 어쩔 수 없이 clone을 잘 작동하도록 구현해야 한다. </br>
그렇지 않은 상황에서는 복사 생성자와 복사 팩터리라는 더 나은 객체 복사 방식을 제공할 수 있다.

### 복사 생성자와 복사 팩터리 방법
객체를 복제하는 방법에는 Cloneable 인터페이스를 구현하는 것 말고도 복사 생성자와 복사 팩터리 방법이 있다.

복사 생성자는 자신과 같은 클래스의 인스턴스를 받는 생성자를 말한다.
```java
public static Yum(Yum yum) {...};
```

복사 팩터리는 item1에서 봤던 것과 동일하게 복사 생성자를 모방한 방식이다.
```java
public static Yum newInstance(Yum yum) {...};
```

복사 생성자와 복사 팩터리는 해당 클래스가 구현한 '인터페이스'타입의 인스턴스를 인수로 받을 수 있다.

이를 이용하면 원본 구현 타입에 얽매이지 않고 복제본 타입을 직접 선택할 수 있다.</br>
예를 들어 HashSet객체를 생성한 다음에 TreeSet 변환 생성자에 매개변수로 전달하면, TreeSet 타입의 객체로 변환하여 복제할 수 있다.

```java
Set set = new HashSet<>();
Set treeSet = new TreeSet(set);
```

## 요약
> - 배열만이 clone 메서드 방식을 가장 깔끔하게 활용할 수 있는 예시다.
> - clone보다는 변환 생성자와 변환 팩터리를 사용하는 것이 권장된다.