package org.jooby.funzy;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.NoSuchElementException;

public class WhenTest {
  @Test
  public void when() {
    Throwing.Function<Object, String> fn = value ->
        new When<>(value)
            .is(Number.class, "Number")
            .is(String.class, "String")
            .get();
    assertEquals("Number", fn.apply(1));
    assertEquals("String", fn.apply("v"));
  }

  @Test(expected = NoSuchElementException.class)
  public void nomatch() {
    Throwing.Function<Object, String> fn = value ->
        new When<>(value)
            .is(Number.class, "Number")
            .is(String.class, "String")
            .get();
    fn.apply(true);
  }

  @Test
  public void safeCast() {
    Throwing.Function<Object, Integer> fn = value ->
        new When<>(value)
            .is(Integer.class, x -> x * 2)
            .orElse(-1);
    assertEquals(2, fn.apply(1).intValue());
    assertEquals(4, fn.apply(2).intValue());
  }

  @Test
  public void mixed() {
    Throwing.Function<Number, String> fn = value ->
        new When<>(value)
            .is(Integer.class, x -> "int")
            .is(Long.class, x -> "long")
            .is(Float.class, "float")
            .is(Double.class, x->"double")
            .orElse("number");
    assertEquals("int", fn.apply(1));
    assertEquals("long", fn.apply(1L));
    assertEquals("float", fn.apply(1f));
    assertEquals("double", fn.apply(1d));
    assertEquals("number", fn.apply((short) 1));
  }
}
