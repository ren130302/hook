package com.ren130302.hook;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class HookDescriptor {

  public static HookDescriptor factory(HookManager hookManager, Hook hook) {
    return new HookDescriptor(hookManager, hook);
  }

  private final HookManager hookManager;
  private final Hook hook;
  private final Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
  private final Map<Signature, Method> resolvedMethods = new HashMap<>();

  public HookDescriptor(HookManager hookManager, Hook hook) {
    this.hookManager = Objects.requireNonNull(hookManager, "hookManager");
    this.hook = Objects.requireNonNull(hook, "hook");

    HookDefine hookDefine = this.getHookDefine();
    for (Signature signature : hookDefine.value()) {
      this.resolve(signature);
    }
  }

  public HookDefine getHookDefine() {
    Class<? extends Hook> hookClass = this.hook.getClass();
    HookDefine hookDefine = hookClass.getAnnotation(HookDefine.class);

    if (hookDefine == null) {
      throw new IllegalStateException(
          "Could not find @HookDefine annotation : " + hookClass.getName());
    }

    return hookDefine;
  }

  public Class<?>[] getHookableInterfacesForTarget(Class<?> type) {
    Set<Class<?>> interfaces = new HashSet<>();
    Set<Class<?>> seen = new HashSet<>();

    while (type != null) {
      for (Class<?> iface : type.getInterfaces()) {
        if (seen.add(iface) && this.signatureMap.containsKey(iface)) {
          interfaces.add(iface);
        }
      }
      type = type.getSuperclass();
    }

    return interfaces.toArray(new Class<?>[0]);
  }


  public Method resolve(Signature signature) {
    Class<?> declaringType = signature.declaringType();

    if (!this.hookManager.isAllowedInterface(declaringType)) {
      throw new IllegalArgumentException("Unsupported target type: " + declaringType.getName()
          + " in " + this.hook.getClass().getName() + ". Allowed types: "
          + this.hookManager.getAllowedInterfaces().stream().map(Class::getName).toList());
    }

    Method method = this.resolvedMethods.computeIfAbsent(signature, MethodResolver::resolve);
    Set<Method> methods = this.signatureMap.computeIfAbsent(declaringType, t -> new HashSet<>());
    methods.add(method);
    return method;
  }

  public boolean isTargetMethod(Method method) {
    Class<?> declaringClass = method.getDeclaringClass();
    Set<Method> methods = this.signatureMap.get(declaringClass);

    return methods != null && methods.contains(method);
  }

  @SuppressWarnings("unchecked")
  public <T> T apply(T target) {
    Class<?> targetClass = target.getClass();
    Class<?>[] interfaces = this.getHookableInterfacesForTarget(targetClass);

    if (interfaces.length > 0) {
      ClassLoader loader = targetClass.getClassLoader();
      HookHandler plugin = new HookHandler(target, this);

      return (T) Proxy.newProxyInstance(loader, interfaces, plugin);
    }

    return target;
  }

  public Hook getHook() {
    return this.hook;
  }

}
