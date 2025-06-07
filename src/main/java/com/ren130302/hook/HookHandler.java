package com.ren130302.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public record HookHandler(Object target, HookInfo hookInfo) implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      if (this.hookInfo.isTargetMethod(method)) {
        Invocation invocation = new Invocation(this.target, method, args);
        return this.hookInfo.hook().intercept(invocation);
      }

      return method.invoke(this.target, args);
    } catch (Exception e) {
      throw unwrapThrowable(e);
    }
  }

  private static Throwable unwrapThrowable(Throwable wrapped) {
    Throwable unwrapped = wrapped;

    while (true) {
      if (unwrapped instanceof InvocationTargetException e) {
        unwrapped = e.getTargetException();
      } else if (unwrapped instanceof UndeclaredThrowableException e) {
        unwrapped = e.getUndeclaredThrowable();
      } else {
        return unwrapped;
      }
    }
  }

}
