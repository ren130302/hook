package com.ren130302.hook;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.ren130302.hook.jdk.DefaultHookHandler;

public record HookDescriptor(Hook hook, HookMetadata hookMetadata)
    implements Comparable<HookDescriptor> {

  private static final Map<Class<?>, Set<Class<?>>> CACHE_INTERFACES = new ConcurrentHashMap<>();

  public static HookDescriptor extract(AllowedInterfaceRegistry allowedInterfaceRegistry,
      Hook hook) {
    Class<?> hookClass = hook.getClass();
    HookDefine hookDefine = hookClass.getAnnotation(HookDefine.class);

    Objects.requireNonNull(hookDefine,
        "Could not find @HookDefine annotation: " + hookClass.getName());
    HookMetadata hookMetadata = HookMetadata.of(hook);
    return new HookDescriptor(hook, hookMetadata);
  }


  private static Set<Class<?>> collectInterfaces(Class<?> type) {
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
    return !HookDescriptor.collectInterfaces(target).isEmpty();
  }

  @SuppressWarnings("unchecked")
  public <T> T bind(T target) {
    Class<?> targetClass = target.getClass();
    Set<Class<?>> collected = HookDescriptor.collectInterfaces(targetClass);

    if (collected.isEmpty()) {
      return target;
    }

    Class<?>[] interfaces = collected.toArray(new Class[0]);
    ClassLoader loader = targetClass.getClassLoader();
    HookHandler handler = new DefaultHookHandler(target, this);
    Object proxyInstance = Proxy.newProxyInstance(loader, interfaces, handler);

    return (T) proxyInstance;
  }

  public void initHook(HookManager hookManager) {
    this.hook().init(hookManager);
  }

  @Override
  public int compareTo(HookDescriptor that) {
    return Integer.compare(this.hookMetadata.getPriority(), that.hookMetadata.getPriority());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HookDescriptor that)) {
      return false;
    }
    return this.hook().getClass().equals(that.hook().getClass());
  }

  @Override
  public int hashCode() {
    return this.hook().getClass().hashCode();
  }

}
