package com.ren130302.hook;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final record HookInfo(Hook hook, HookDefine define,
    Map<SignatureInfo, Method> signatureInfos, Map<Class<?>, Set<Method>> signatureMap)
    implements Comparable<HookInfo> {

  private Class<?>[] getHookableInterfacesForTarget(Class<?> type) {
    Set<Class<?>> interfaces = new HashSet<>();

    while (type != null) {
      for (Class<?> c : type.getInterfaces()) {
        if (this.signatureMap.containsKey(c)) {
          interfaces.add(c);
        }
      }
      type = type.getSuperclass();
    }

    return interfaces.toArray(new Class<?>[0]);
  }

  @SuppressWarnings("unchecked")
  public <T> T plugin(T target) {
    Class<?> targetClass = target.getClass();
    Class<?>[] interfaces = this.getHookableInterfacesForTarget(targetClass);

    if (interfaces.length > 0) {
      ClassLoader loader = targetClass.getClassLoader();
      HookHandler plugin = new HookHandler(target, this);

      return (T) Proxy.newProxyInstance(loader, interfaces, plugin);
    }

    return target;
  }

  public Signature[] signatures() {
    return this.define.value();
  }

  public byte priority() {
    return this.define.priority();
  }

  @Override
  public int compareTo(HookInfo o) {
    return Byte.compare(this.priority(), o.priority());
  }

  public static HookInfo from(Hook hook, Set<Class<?>> hookableInterfaces) {
    Class<?> hookClass = hook.getClass();
    HookDefine define = hookClass.getAnnotation(HookDefine.class);

    if (define == null) {
      throw new IllegalStateException(
          "Could not find @HookDefine annotation : " + hookClass.getName());
    }

    Map<SignatureInfo, Method> signatureInfos = new HashMap<>();
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();

    for (Signature signature : define.value()) {
      Class<?> type = signature.type();

      if (!hookableInterfaces.contains(type)) {
        throw new IllegalArgumentException(
            "Unsupported target type: " + type.getName() + " at " + hookClass.getName());
      }

      SignatureInfo signatureInfo = new SignatureInfo(signature);

      Method method = signatureInfo.getMethod();
      Set<Method> methods = signatureMap.computeIfAbsent(type, t -> new HashSet<>());

      signatureInfos.put(signatureInfo, method);
      methods.add(method);
    }

    return new HookInfo(hook, define, Map.copyOf(signatureInfos), Map.copyOf(signatureMap));
  }
}
