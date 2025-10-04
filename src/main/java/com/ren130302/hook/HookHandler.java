package com.ren130302.hook;

import java.lang.reflect.InvocationHandler;

public interface HookHandler extends InvocationHandler {

  Object target();

}
