package com.ren130302.hook.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class AllowedInterfaceRegistry {

  private final Set<Class<?>> knownClasses = new HashSet<>();

  public AllowedInterfaceRegistry add(Class<?> iface) {
    Objects.requireNonNull(iface, "interface must not be null");
    if (!iface.isInterface()) {
      throw new IllegalArgumentException("Not an interface: " + iface.getName());
    }

    this.knownClasses.add(iface);

    return this;
  }

  public AllowedInterfaceRegistry add(Class<?>... ifaces) {
    Objects.requireNonNull(ifaces, "ifaces must not be null");
    for (Class<?> iface : ifaces) {
      this.add(iface);
    }
    return this;
  }

  public AllowedInterfaceRegistry add(Collection<Class<?>> ifaces) {
    Objects.requireNonNull(ifaces, "ifaces must not be null");
    ifaces.forEach(this::add);
    return this;
  }

  public boolean has(Class<?> iface) {
    return this.knownClasses.contains(iface);
  }

}
