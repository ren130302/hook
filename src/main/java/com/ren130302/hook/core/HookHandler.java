package com.ren130302.hook.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import com.ren130302.hook.api.Invocation;

record HookHandler(Object target, HookDescriptor hookDescriptor) implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      if (this.hookDescriptor.containsMethod(method)) {
        Invocation invocation = new Invocation(this.target, method, args);
        return this.hookDescriptor.applyHook(invocation);
      }

      return method.invoke(this.target, args);
    } catch (Exception e) {
      throw unwrapThrowable(e);
    }
  }

  static Throwable unwrapThrowable(Throwable wrapped) {
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
