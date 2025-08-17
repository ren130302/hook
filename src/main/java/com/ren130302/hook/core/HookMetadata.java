package com.ren130302.hook.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.ren130302.hook.api.Hook;
import com.ren130302.hook.api.HookDefine;
import com.ren130302.hook.api.Signature;

public record HookMetadata(Class<? extends Hook> hookClass, HookDefine hookDefine,
    Map<Class<?>, Set<Method>> knownMethods) {

  public static HookMetadata of(Hook hook) {
    Objects.requireNonNull(hook, "hook must not be null");
    Class<? extends Hook> hookClass = hook.getClass();
    HookDefine hookDefine = hookClass.getAnnotation(HookDefine.class);

    Objects.requireNonNull(hookDefine, "Missing @HookDefine annotation on " + hookClass.getName());

    Map<Class<?>, Set<Method>> knownMethods = new HashMap<>();

    Signature[] signatures = hookDefine.value();
    Objects.requireNonNull(signatures, "signatures must not be null");

    for (Signature signature : signatures) {
      Objects.requireNonNull(signature, "signature must not be null");
      Class<?> declaringType = signature.declaringType();

      Method method = resolve(signature);
      Set<Method> methods = knownMethods.computeIfAbsent(declaringType, t -> new HashSet<>());
      methods.add(method);
    }

    Map<Class<?>, Set<Method>> immutableKnownMethods = knownMethods.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));

    return new HookMetadata(hookClass, hookDefine, immutableKnownMethods);
  }

  public int getPriority() {
    return this.hookDefine.priority();
  }

  public Signature[] getSignatures() {
    return this.hookDefine.value().clone();
  }

  public boolean containsClass(Class<?> iface) {
    return this.knownMethods.containsKey(iface);
  }

  public boolean containsMethod(Method method) {
    Class<?> cls = method.getDeclaringClass();
    Set<Method> methods = this.knownMethods.getOrDefault(cls, Set.of());

    return methods.contains(method);
  }

  private static Method resolve(Signature signature) {
    Class<?> declaringType = signature.declaringType();
    String methodName = signature.methodName();
    Class<?>[] parameterTypes = signature.parameterTypes();
    try {
      return declaringType.getMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(String.format("Could not resolve method %s.%s(%s)",
          declaringType.getName(), methodName, Arrays.toString(parameterTypes)), e);
    }
  }

}
