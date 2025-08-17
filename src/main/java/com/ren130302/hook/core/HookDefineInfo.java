package com.ren130302.hook.core;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.ren130302.hook.api.Hook;
import com.ren130302.hook.api.HookDefine;
import com.ren130302.hook.api.HookHandler;
import com.ren130302.hook.api.HookManager;
import com.ren130302.hook.api.Invocation;

public record HookDefineInfo(Hook hook, HookMetadata hookMetadata) {

  private static final Map<Class<?>, Set<Class<?>>> CACHE_INTERFACES = new ConcurrentHashMap<>();

  public static HookDefineInfo extract(AllowedInterfaceRegistry allowedInterfaceRegistry,
      Hook hook) {
    Class<?> hookClass = hook.getClass();
    HookDefine hookDefine = hookClass.getAnnotation(HookDefine.class);

    Objects.requireNonNull(hookDefine,
        "Could not find @HookDefine annotation: " + hookClass.getName());
    HookMetadata hookMetadata = HookMetadata.of(hook);
    return new HookDefineInfo(hook, hookMetadata);
  }


  public Set<Class<?>> collectInterfaces(Class<?> type) {
    return CACHE_INTERFACES.computeIfAbsent(type, t -> {
      Set<Class<?>> interfaces = new HashSet<>();

      for (Class<?> c = t; c != null; c = c.getSuperclass()) {
        Collections.addAll(interfaces, c.getInterfaces());
      }

      return Collections.unmodifiableSet(interfaces);
    });
  }

  public Object applyHook(Invocation invocation) throws Throwable {
    return this.hook().apply(invocation);
  }

  public boolean isTargetClass(Class<?> target) {
    return !this.collectInterfaces(target).isEmpty();
  }

  @SuppressWarnings("unchecked")
  public <T> T bind(T target) {
    Class<?> targetClass = target.getClass();
    Set<Class<?>> collected = this.collectInterfaces(targetClass);

    if (collected.isEmpty()) {
      return target;
    }

    Class<?>[] interfaces = collected.toArray(new Class[0]);
    ClassLoader loader = targetClass.getClassLoader();
    HookHandler handler = new HookHandlerImpl(target, this);
    Object proxyInstance = Proxy.newProxyInstance(loader, interfaces, handler);

    return (T) proxyInstance;
  }

  public void initHook(HookManager hookManager) {
    this.hook().init(hookManager);
  }
}
