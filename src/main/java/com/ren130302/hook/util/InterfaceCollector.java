package com.ren130302.hook.util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class InterfaceCollector {

  private InterfaceCollector() {}

  public static Class<?>[] collect(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
    Set<Class<?>> interfaces = new HashSet<>();
    Set<Class<?>> seen = new HashSet<>();

    while (type != null) {
      for (Class<?> iface : type.getInterfaces()) {
        if (seen.add(iface) && signatureMap.containsKey(iface)) {
          interfaces.add(iface);
        }
      }
      type = type.getSuperclass();
    }

    return interfaces.toArray(new Class<?>[0]);
  }
}
