package com.ren130302.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final record Invocation(Object target, Method method, Object[] args) {

  public Object proceed() throws InvocationTargetException, IllegalAccessException {
    return this.method.invoke(this.target, this.args);
  }

}
