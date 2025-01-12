`try-finally`보다는 `try-with-resources`를 사용하라
## 자원을 닫는 방법
- Java 라이브러리에는 `Input/OutputStream`, `SQL Connection` 등 직접 닫아야 하는 자원들이 많음
- 이러한 자원들은 자칫 닫는것을 놓치기 쉬움
- 전통적으로 *자원이 닫힘을 보장*하기 위해 `try-finally` 문을 사용해왔으나, 후술할 **문제점** 때문에 **Java 7**에서 `try-with-resources`이 등장한 이후로는 이를 사용
### `try-finally`
- `try-finally`문을 사용해 try 문 내부에서 *문제가 발생*해도, `finally 문`에서 *자원이 닫힐 수 있도록* 하는 방법
#### 문제 1 - 예외 처리 및 디버깅
```java
// try-finally 문을 사용한 예시
static String firstLineOfFile(String path) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(path));
	try {
		return br.readLine();
	} finally {
		// try 문 내부에서 예외가 발생해도, 항상 자원이 닫힘을 보장
		br.close();
	}
}
```
- 위 구문은 `try-finally`를 통해 자원이 항상 닫힘을 보장하는 평범한 코드 같지만, 문제점이 존재
- 물리적인 문제 등을 이유로 `예외`가 `try문`(`readLine`), `finally문`(`close`) **모두에서 발생**할 경우, `finally문`에서 발생한 예외가 `앞선 예외를 "지워버림"`
	- 즉, `스택 추적(Stack Trace) 내역`에 *앞선 예외에 관한 정보*가 남지않아 **`디버깅이 힘들어짐`**
		- 코드를 추가해 *앞선 예외를 대신 기록*하도록 할 수도 있지만, `코드가 지저분`해짐
#### 문제2 - 두 개 이상의 자원 사용
```java
// 사용하는 자원이 두 개 이상인 경우
static void copy(String src, String dst) throws IOException {
	InputStream in = new FileInputStream(src);
	try {
		OutputStream out = new FileOutputStream(dst);
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			int n;
			while ((n = in.read(buf)) >= 0)
			out.write(buf, 0, n);
		} finally {
			out.close();
		}
	}
	finally {
		in.close();
	}
}
```
- `try-finally`문은 **두 개 이상의 자원**을 사용할 경우 그 갯수 만큼 `try-finally` 블록 쌍의 갯수가 늘어나 `코드 가독성을 저하`시킴
### `try-with-resources`
- [Java 7에서 등장한 방식](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)으로, *구문이 끝난 뒤* `선언된 자원들을 닫는 방식`
- [java.io.Closeable](https://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html) 및 [java.lang.Autocloseable](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html)을 구현하는 모든 객체들을 자원으로 사용할 수 있음
	- 둘 모두 단순히 void 타입의 `close` 메서드 하나만 정의
#### 문제 1 코드 개선
```java
static String firstLineOfFile(String path) throws IOException {
	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
		return br.readLine();
	}
}
```
- `try-with-resources`를 사용하게 되면, 앞선 *`try-finally`와 다르게* 이후에 발생한 예외는 숨겨지고, `앞서 발생한 예외`가 **기록**됨
	- 즉, 위 케이스에서 앞서 말했던 `readLine`과 `close`(위 코드에는 드러나있지 않음) 모두에서 예외 발생시 `close`에서 발생한 예외는 숨겨지고, `readLine`에서 발생한 예외가 **기록**됨
		- 이처럼 실제 사례에서는 출력될 `예제 하나만 보존`되고, `나머지 예외`는 *숨겨져 있을 수 있음*
	- `숨겨진 예외`들은 버려지지 않고, `스택 추적 내역`에 `suppressed` 표시를 달고 출력됨
		- 게다가, Java 7에서 `Throwable`에 추가된 `getSuppressed` 메서드를 통해 가져올 수도 있음
#### 문제 2 코드 개선
```java
static void copy(String src, String dst) throws IOException {
	try (InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst)) {
	byte[] buf = new byte[BUFFER_SIZE];
	int n;
	while ((n = in.read(buf)) >= 0)
		out.write(buf, 0, n);
	}
}
```
- 코드가 이전보다 간략해짐
#### 추가 - catch와 함께 사용하기
```java
static String firstLineOfFile(String path, String defaultVal) {
	try (BufferedReader br = new BufferedReader(
			new FileReader(path))) {
		return br.readLine();
	} catch (IOException e) {
		// 파일을 열거나, 데이터를 읽지 못했을 때 예외 대신
		// 인자로 받은 문자열을 반환하도록 하는 예시
		return defaultVal;
	}
}
```
- 또한, `try-with-resources`문 역시 `catch` 문을 사용할 수 있음
	- 따라서 내부에 *또 다른 try문을 중첩하지 않고* `다수의 예외를 처리`할 수 있음

# 핵심
- 회수가 필수적인 자원은 무조건  `try-finally`가 아닌, `try-with-resources`를 사용하자
	- 코드를 간단하게 만들고, 예외 정보도 더 유용해짐