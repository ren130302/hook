package com.ren130302.hook.jdk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import com.ren130302.hook.DefaultInvocation;
import com.ren130302.hook.HookDescriptor;
import com.ren130302.hook.HookHandler;
import com.ren130302.hook.HookHandlerException;
import com.ren130302.hook.Invocation;

public record DefaultHookHandler(Object target, HookDescriptor hookDefineInfo)
    implements HookHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Invocation invocation = new DefaultInvocation(this.target, method, args);
    try {
      if (this.hookDefineInfo.hookMetadata().containsMethod(method)) {
        return this.hookDefineInfo.applyHook(invocation);
      }
      return invocation.proceed();
    } catch (Exception e) {
      throw new HookHandlerException("Signature :" + invocation, unwrapThrowable(e));
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
