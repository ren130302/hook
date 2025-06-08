package com.ren130302.hook.core;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import com.ren130302.hook.api.Hook;
import com.ren130302.hook.util.InterfaceCollector;

final class HookDescriptor {

  private final Hook hook;
  private final MethodRegistry methodRegistry;

  private final Map<Class<?>, Class<?>[]> interfaceCache = new WeakHashMap<>();

  public HookDescriptor(Hook hook, MethodRegistry methodRegistry) {
    this.hook = hook;
    this.methodRegistry = methodRegistry;
  }

  public Class<?>[] getHookableInterfacesForTarget(Class<?> type) {
    return this.interfaceCache.computeIfAbsent(type,
        t -> InterfaceCollector.collect(t, this.methodRegistry.getSignatureMap()));
  }

  public boolean isTargetMethod(Method method) {
    return this.methodRegistry.containsMethod(method);
  }

  public <T> T apply(T target) {
    return HookProxyFactory.createIfApplicable(target, this);
  }

  public Hook getHook() {
    return this.hook;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof HookDescriptor that)) {
      return false;
    }
    return this.hook.equals(that.hook);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.hook);
  }

}
