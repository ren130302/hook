package com.ren130302.hook.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import com.ren130302.hook.api.Invocation;
import com.ren130302.hook.util.ThrowableUnwrapper;

public record HookHandler(Object target, HookDescriptor hookDescriptor)
    implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      if (this.hookDescriptor.isTargetMethod(method)) {
        Invocation invocation = new Invocation(this.target, method, args);
        return this.hookDescriptor.getHook().apply(invocation);
      }

      return method.invoke(this.target, args);
    } catch (Exception e) {
      throw ThrowableUnwrapper.unwrap(e);
    }
  }

}
