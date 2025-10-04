package com.ren130302.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Invocation {

  Object target();

  Method method();

  Object[] args();

  Object proceed() throws InvocationTargetException, IllegalAccessException;

  @Override
  String toString();

}
