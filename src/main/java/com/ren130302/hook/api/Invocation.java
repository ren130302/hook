package com.ren130302.hook.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record Invocation(Object target, Method method, Object[] args) {

  public Object proceed() throws InvocationTargetException, IllegalAccessException {
    return this.method.invoke(this.target, this.args);
  }

}
