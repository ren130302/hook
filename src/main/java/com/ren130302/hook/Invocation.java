package com.ren130302.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public record Invocation(Object target, Method method, Object[] args) {

  public Object proceed() throws InvocationTargetException, IllegalAccessException {
    return this.method.invoke(this.target, this.args);
  }

  @Override
  public String toString() {
    Class<?> declaringClass = this.method.getDeclaringClass();
    Class<?>[] parameterTypes = this.method.getParameterTypes();

    String className = declaringClass.getName();
    String methodName = this.method.getName();
    String paramValues =
        Arrays.stream(parameterTypes).map(Objects::toString).collect(Collectors.joining(", "));

    return "Invocation[" + className + "#" + methodName + "(" + paramValues + ")]";
  }

}
