package com.ren130302.hook;

import java.lang.reflect.Method;

public final class MethodResolver {

  private MethodResolver() {}

  public static Method resolve(Signature signature) {
    try {
      Class<?> declaringType = signature.declaringType();
      String methodName = signature.methodName();
      Class<?>[] parameterTypes = signature.parameterTypes();

      Method method = declaringType.getMethod(methodName, parameterTypes);
      return method;
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "Could not find method for signature : " + signature + ". Cause: " + e, e);
    }
  }

}
