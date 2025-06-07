package com.ren130302.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Set;

public record HookHandler(Object target, HookInfo hookInfo) implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Class<?> declaringClass = method.getDeclaringClass();
      Set<Method> methods = this.hookInfo.signatureMap().get(declaringClass);

      if (methods != null && methods.contains(method)) {
        Invocation invocation = new Invocation(this.target, method, args);

        return this.hookInfo.hook().intercept(invocation);
      }

      return method.invoke(this.target, args);
    } catch (Exception e) {
      throw unwrapThrowable(e);
    }
  }

  public static Throwable unwrapThrowable(Throwable wrapped) {
    Throwable unwrapped = wrapped;

    while (true) {
      if (unwrapped instanceof InvocationTargetException) {
        unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
      } else if (unwrapped instanceof UndeclaredThrowableException) {
        unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
      } else {
        return unwrapped;
      }
    }
  }


}
