package com.ren130302.hook.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.ren130302.hook.api.HookHandler;
import com.ren130302.hook.api.Invocation;

public record InvocationImpl(Object target, Method method, Object[] args) implements Invocation {

  private static final Collector<CharSequence, ?, String> JOINING_COMMA = Collectors.joining(", ");

  @Override
  public Object proceed() throws InvocationTargetException, IllegalAccessException {
    return this.method.invoke(this.target, this.args);
  }

  @Override
  public final String toString() {
    Class<?> targetClass = unproxy(this.target).getClass();
    String className = targetClass.getName();
    Class<?>[] parameterTypes = this.method.getParameterTypes();
    String methodName = this.method.getName();
    String paramTypeNames =
        Arrays.stream(parameterTypes).map(Class::getName).collect(JOINING_COMMA);

    return "" + className + "#" + methodName + "(" + paramTypeNames + ")";
  }


  static Object unproxy(Object proxyTarget) {
    Object current = proxyTarget;

    while (Proxy.isProxyClass(current.getClass())) {
      InvocationHandler handler = Proxy.getInvocationHandler(current);
      if (!(handler instanceof HookHandler hookHandler)) {
        break;
      }
      current = hookHandler.target();
    }

    return current;
  }

}
