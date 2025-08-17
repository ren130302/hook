package com.ren130302.hook.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import com.ren130302.hook.api.HookHandler;
import com.ren130302.hook.api.Invocation;

public record HookHandlerImpl(Object target, HookDefineInfo hookDefineInfo) implements HookHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      if (this.hookDefineInfo.hookMetadata().containsMethod(method)) {
        Invocation invocation = new InvocationImpl(this.target, method, args);
        return this.hookDefineInfo.applyHook(invocation);
      }
      return method.invoke(this.target, args);
    } catch (Exception e) {
      throw unwrapThrowable(e);
    }
  }

  static Throwable unwrapThrowable(Throwable wrapped) {
    Throwable current = wrapped;

    while (true) {
      if (current instanceof InvocationTargetException e) {
        current = e.getTargetException();
      } else if (current instanceof UndeclaredThrowableException e) {
        current = e.getUndeclaredThrowable();
      } else {
        return current;
      }
    }
  }

}
