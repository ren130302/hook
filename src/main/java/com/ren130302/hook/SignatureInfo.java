package com.ren130302.hook;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public record SignatureInfo(Signature signature) {

  private static final Collector<CharSequence, ?, String> JOINING_COMMA = Collectors.joining(", ");

  public Class<?> type() {
    return this.signature.type();
  }

  public String name() {
    return this.signature.name();
  }

  public Class<?>[] paramTypes() {
    return this.signature.paramTypes();
  }

  public Method getMethod() {
    try {
      return this.type().getMethod(this.name(), this.paramTypes());
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "Could not find method for signature : " + this + ". Cause: " + e, e);
    }
  }

  @Override
  public String toString() {
    String className = this.type().getName();
    String methodName = this.name();
    String paramTypeNames =
        Arrays.stream(this.paramTypes()).map(Class::getName).collect(JOINING_COMMA);

    return "Signature[" + className + "#" + methodName + "(" + paramTypeNames + ")]";
  }

}
