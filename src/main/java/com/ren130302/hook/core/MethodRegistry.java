package com.ren130302.hook.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.ren130302.hook.api.Signature;
import com.ren130302.hook.util.MethodResolver;

final class MethodRegistry {
  private final HookManager hookManager;
  private final Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
  private final Map<Signature, Method> resolvedMethods = new HashMap<>();

  public MethodRegistry(HookManager hookManager) {
    this.hookManager = hookManager;
  }

  public Method resolve(Signature signature) {
    Class<?> declaringType = signature.declaringType();
    if (!this.hookManager.isAllowedInterface(declaringType)) {
      throw new IllegalArgumentException("Unsupported target type: " + declaringType);
    }

    Method method = this.resolvedMethods.computeIfAbsent(signature, MethodResolver::resolve);
    this.signatureMap.computeIfAbsent(declaringType, t -> new HashSet<>()).add(method);
    return method;
  }

  public boolean containsMethod(Method method) {
    Set<Method> methods = this.signatureMap.get(method.getDeclaringClass());
    return methods != null && methods.contains(method);
  }

  public Map<Class<?>, Set<Method>> getSignatureMap() {
    return Map.copyOf(this.signatureMap);
  }
}
