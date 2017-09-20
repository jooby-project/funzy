[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jooby/funzy/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jooby/funzy)
# funzy
Functional Idioms for Java 8. Funzy is a small/zero-dependencies library with two specific goals:

- Provides functional idioms for traditional Java statements
- Simplify exception handling and reduces the noise of checked exception while working with lambdas.

## design

- This library make use of the `sneakyThrow` technique, which basically let you `throw` checked exceptions like they were `runtime` exceptions.

## when idiom

* Basic example:

```java
import org.jooby.funzy.When.when;

...
Object value = ...;

String result = when(value)
    .is(1, "Got a number")
    .is(2, "Got a string")
    .get();

System.out.println(result);
```

* Auto-cast example:

```java
import org.jooby.funzy.When.when;

...
Object value = ...;

int result = when(value)
    .is(Integer.class, x -> x * 2)
    .orElse(-1);

System.out.println(result);
```

* Throw an exception if no match:

```java
import org.jooby.funzy.When.when;

...
Object value = ...;

String result = when(value)
    .is(Integer.class, "int")
    .is(Long.class, "long")
    .is(Float.class, "float")
    .is(Double.class, "double")
    .orElseThrow(() -> new IllegalArgumentException("NaN"));

System.out.println(result);
```

* Advanced usage via predicates

```java
import org.jooby.funzy.When.when;

...
String value = ...;

String result = when(value)
    .is(s -> s.startsWith("http://"), this::parseUrl)
    .is(s -> s.startsWith("ftp://"), this::parseUrl)
    .orElseThrow(() -> new IllegalArgumentException("Unknown protocol: " + value));

System.out.println(result);
```

## Try idiom

* Functional try:

Get a computed value or `throw` an exception (checked or not):

```java
String value = ...;

int number = Try.apply(() -> {
    return Integer.parseInt(value);
  })
  .get();
```


* Side effect try:

Run some exceptional code and rethrow the exception (checked or not) in case of failure.

```java
String value = ...;

Try.run(() -> {
  runSomeExceptionalCode();
}).throwException();
```

### onSuccess, onFailure and onComplete

Complete callback, executed success or failure (works as finally clause):

```java
String value = ...;

Try.apply(() -> {
  return someExceptionalCode();
}).onSuccess(() -> {
  System.out.println("Success");
}).onFailure(x -> {
  System.out.println("Exception ");
  x.printStacktrace();
}).onComplete(() -> {
  System.out.println("Always executed");
})
.get();
```

### recover, orElse, orElseGet

Recover:

```java
String value = ...;

Try.apply(() -> {
  return someExceptionalCode();
}).recover(x -> {
  System.out.println("Got exception " + x);
  return recoverValue();
})
.get();
```

Recover from specific exception:

```java
String value = ...;

Try.apply(() -> {
  return someExceptionalCode();
}).recover(SomeException.class, x -> {
  System.out.println("Got SomeException");
  return recoverValue();
})
.get();
```

Default result value:

```java
String value = Try.apply(() -> {
  return someExceptionalCode();
})
.orElse("default value");
```

Default lazy result value:

```java
String value = Try.apply(() -> {
  return someExceptionalCode();
})
.orElseGet(() -> computeDefaultValue());
```

### unwrap

Invoke a method using reflection and get the result or `unwrap` `InvocationTargetException.class` and rethrow the `casue`

```java
Object value = Try.apply(() -> {
  Method method = ...;
  return method.invoke(...);
}).unwrap(InvocationTargetException.class)
  .get();
```

### wrap

Wrap any potential exception under a common/custom exception:

```java
Object value = Try.apply(() -> {
  return someExceptionalCode();
}).wrap(x -> new IllegalStateException("Caught exception", x))
  .get();
```

## Try-with-resources idiom

* Copy two streams:

```java
Try.of(in, out)
  .run((from, to) -> copy(from, to))
  .throwException();
```

* Run jdbc query:

```java
Try.with(() -> newConnection())
  .map(connection -> connection.preparedStatement("select * from awesome-funzy"))
  .map(statement -> statement.executeQuery())
  .apply(resultSet -> {
    ...
  })
  .get();
```

## Throwable interfaces

We do provide throwable 100% interfaces for:

- Consumer
- Function
- Supplier
- Predicate
- BiFunction
- BiConsumer

* File read example:
```java
import static org.jooby.funzy.Throwing.throwingFunction;

Throwing.Function<String, String> fn = ;

Arrays.asList("f1.txt", "f2.txt")
  .stream()
  .map(throwingFunction(file -> {
         return readContent(file);
  }))
  .collect(Collectors.toList());
```

Additional 8 arguments version for Consumer and Function. All them accessible via `Throwing` class:

## dependency

### maven

```xml
<dependency>
  <groupId>org.jooby</groupId>
  <artifactId>funzy</artifactId>
  <version>0.1.0</version>
</dependency>
```

### gradle

```
compile: 'org.jooby:funzy:0.1.0'
```

## license

[Apache License 2](http://www.apache.org/licenses/LICENSE-2.0.html)
