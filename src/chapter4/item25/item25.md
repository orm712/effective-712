# ITEM 25. 탑레벨 클래스는 한 파일에 하나만 담아라.

## 탑레벨 클래스란

> Java에서 탑 레벨 클래스(top-level class)는 다른 클래스에 의해 포함되지 않는 클래스를 말합니다. 즉, 이들은 다른 클래스의 내부에 위치하지 않고 독립적으로 선언됩니다. Java 파일(*
> .java) 당 하나의 탑 레벨 공용 클래스가 있을 수 있으며, 파일 이름은 이 공용 클래스의 이름과 일치해야 합니다.

## 본문

소스 파일 하나에 탑레벨 클래스를 여러 개 선언하더라도 컴파일러는 문제를 일으키지 않는다.
하지만 아무런 득이 없을 뿐더러 심각한 위험을 감수해야 하는 행위이다.
위험의 이유는 어느 소스 파일을 먼저 컴파일하냐에 따라 달라진다.

<예시 메인문>

이 Main을 실행하면 pancake가 출력된다.

문제가 발생하는 경우는 Dessert.java가 추가되면 생긴다.

이게 책에서는 javac로 컴파일하면 안된다고 했는데 컴파일이 ㅈㄴ잘된다;;
일단 결론은 컴파일할 때 넘겨준 소스 순서에 따라 동작이 달라지므로 매우 위험하다고 한다.

해결책은? 그냥 따로 클래스 만드는거임.
굳~~이 한 파일에 넣어야겠다면 정적 멤버 클래스를 사용해서 inner로 처리하던가 하자. 한 파일에 넣게 되는 경우는 한 클래스가 다른 한 클래스에 딸린 부차적인 클래스 인 경우가 많으므로 inner로 처리할 경우
생각보다 맞는 구현일 경우가 많을 것이다. 읽기도 좋고 private inner class로 하면 접근 범위도 최소한으로 관리할 수 있기 때문이다.

> 핵심정리
> **소스 파일 하나에는 반드시 톱레벨 클래스(혹은 인터페이스)를 하나만 담자.**
