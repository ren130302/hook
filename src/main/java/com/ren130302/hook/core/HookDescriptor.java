package com.ren130302.hook.core;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.ren130302.hook.api.Hook;
import com.ren130302.hook.api.HookDefine;
import com.ren130302.hook.api.Invocation;
import com.ren130302.hook.api.Signature;

final class HookDescriptor {

  private static final Map<Signature, Method> RESOLVED_METHODS_CACHE = new ConcurrentHashMap<>();

  public static HookDescriptor create(HookManager hookManager, Hook hook) {
    Objects.requireNonNull(hookManager, "hookManager must not be null");
    Objects.requireNonNull(hook, "hook must not be null");
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();

    HookDefine hookDefine = extractHookDefine(hook);

    for (Signature signature : hookDefine.value()) {
      Class<?> declaringType = signature.declaringType();
      if (!hookManager.isAllowedInterface(declaringType)) {
        throw new IllegalArgumentException("Unsupported target type: " + declaringType);
      }

      Method method = RESOLVED_METHODS_CACHE.computeIfAbsent(signature, HookDescriptor::resolve);
      Set<Method> methods = signatureMap.computeIfAbsent(declaringType, t -> new HashSet<>());
      methods.add(method);
    }

    Map<Class<?>, Set<Method>> immutableSignatureMap = signatureMap.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));

    return new HookDescriptor(hook, immutableSignatureMap);
  }

  static HookDefine extractHookDefine(Hook hook) {
    HookDefine hookDefine = hook.getClass().getAnnotation(HookDefine.class);
    if (hookDefine == null) {
      throw new IllegalStateException(
          "Could not find @HookDefine annotation: " + hook.getClass().getName());
    }
    return hookDefine;
  }

  static Method resolve(Signature signature) {
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

  private final Hook hook;
  private final Map<Class<?>, Set<Method>> signatureMap;

  private final Map<Class<?>, Class<?>[]> interfaceCache = new WeakHashMap<>();

  private HookDescriptor(Hook hook, Map<Class<?>, Set<Method>> signatureMap) {
    this.hook = hook;
    this.signatureMap = signatureMap;
  }

  @SuppressWarnings("unchecked")
  public <T> T apply(T target) {
    Class<?> targetClass = target.getClass();
    Class<?>[] interfaces =
        this.interfaceCache.computeIfAbsent(targetClass, this::collectInterfaces);

    if (interfaces.length == 0) {
      return target;
    }

    ClassLoader loader = targetClass.getClassLoader();
    HookHandler handler = new HookHandler(target, this);

    return (T) Proxy.newProxyInstance(loader, interfaces, handler);
  }

  public boolean containsClass(Class<?> iface) {
    return this.signatureMap.containsKey(iface);
  }

  public boolean containsMethod(Method method) {
    return this.signatureMap.getOrDefault(method.getDeclaringClass(), Set.of()).contains(method);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof HookDescriptor that)) {
      return false;
    }
    return this.hook.getClass().equals(that.hook.getClass());
  }

  public Object applyHook(Invocation invocation) throws Throwable {
    return this.hook.apply(invocation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.hook.getClass());
  }

  Class<?>[] collectInterfaces(Class<?> type) {
    Set<Class<?>> interfaces = new HashSet<>();
    Set<Class<?>> visitedClass = new HashSet<>();

    while (type != null) {
      for (Class<?> iface : type.getInterfaces()) {
        if (visitedClass.add(iface) && this.containsClass(iface)) {
          interfaces.add(iface);
        }
      }
      type = type.getSuperclass();
    }

    return interfaces.toArray(new Class<?>[0]);
  }

}
