package com.ren130302.hook.api;

import java.lang.reflect.InvocationHandler;

public interface HookHandler extends InvocationHandler {

  Object target();

}
