package com.ren130302.hook.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

public final class ThrowableUnwrapper {

  private ThrowableUnwrapper() {}

  public static Throwable unwrap(Throwable wrapped) {
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
