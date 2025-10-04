package com.ren130302.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final record DefaultInvocation(Object target, Method method, Object[] args)
    implements Invocation {

  private static final Collector<CharSequence, ?, String> JOINING_COMMA = Collectors.joining(", ");

  @Override
  public Object proceed() throws InvocationTargetException, IllegalAccessException {
    return this.method.invoke(this.target, this.args);
  }

  @Override
  public String toString() {
    Object unproxyTarget = this.unproxyTarget();
    Class<?> targetClass = unproxyTarget.getClass();
    String className = targetClass.getName();
    Class<?>[] parameterTypes = this.method.getParameterTypes();
    String methodName = this.method.getName();
    String paramTypeNames =
        Arrays.stream(parameterTypes).map(Class::getName).collect(JOINING_COMMA);

    return "" + className + "#" + methodName + "(" + paramTypeNames + ")";
  }


  public Object unproxyTarget() {
    Object current = this.target;

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
