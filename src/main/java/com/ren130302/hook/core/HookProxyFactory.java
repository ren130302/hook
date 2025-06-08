package com.ren130302.hook.core;

import java.lang.reflect.Proxy;

final class HookProxyFactory {

  private HookProxyFactory() {}

  @SuppressWarnings("unchecked")
  public static <T> T createIfApplicable(T target, HookDescriptor descriptor) {
    Class<?> targetClass = target.getClass();
    Class<?>[] interfaces = descriptor.getHookableInterfacesForTarget(targetClass);

    if (interfaces.length == 0) {
      return target;
    }

    ClassLoader loader = targetClass.getClassLoader();
    HookHandler handler = new HookHandler(target, descriptor);

    return (T) Proxy.newProxyInstance(loader, interfaces, handler);
  }
}
