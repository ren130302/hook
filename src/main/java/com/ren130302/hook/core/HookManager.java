package com.ren130302.hook.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import com.ren130302.hook.api.Hook;

public final class HookManager {

  public static HookManager createWithAllowedInterfaces(Class<?>... interfaces) {
    Objects.requireNonNull(interfaces, "interfaces must not be null");
    Set<Class<?>> allowedInterfaces = new HashSet<>();

    for (Class<?> iface : interfaces) {
      Objects.requireNonNull(iface, "interface must not be null");

      allowedInterfaces.add(iface);
    }

    return new HookManager(Set.copyOf(allowedInterfaces));
  }


  private final Set<Class<?>> allowedInterfaces;
  private final Set<HookDescriptor> hooks = new HashSet<>();

  private HookManager(Set<Class<?>> allowedInterfaces) {
    this.allowedInterfaces = allowedInterfaces;
  }

  public void addHooks(Collection<Hook> hooks) {
    Objects.requireNonNull(hooks, "hooks must not be null");

    for (Hook hook : hooks) {
      this.hooks.add(HookDescriptor.create(this, hook));
    }
  }

  public void addHooks(Hook... hooks) {
    Objects.requireNonNull(hooks, "hooks must not be null");

    for (Hook hook : hooks) {
      this.hooks.add(HookDescriptor.create(this, hook));
    }
  }

  public void addHooksFromServiceLoader() {
    for (Hook hook : ServiceLoader.load(Hook.class)) {
      this.addHooks(hook);
    }
  }

  public boolean isAllowedInterface(Class<?> type) {
    return this.allowedInterfaces.contains(type);
  }

  public <T> T pluginAll(T target) {
    for (HookDescriptor hookDescriptor : this.hooks) {
      target = hookDescriptor.apply(target);
    }
    return target;
  }

}
