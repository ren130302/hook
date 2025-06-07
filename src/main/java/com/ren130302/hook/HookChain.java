package com.ren130302.hook;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public final class HookChain {

  public static HookChain allowedHookInterfaces(Class<?>... interfaces) {
    return new HookChain(Set.of(interfaces));
  }

  private final Set<Class<?>> hookableInterfaces;
  private final Map<Class<? extends Hook>, HookInfo> hooks = new HashMap<>();

  private HookChain(Set<Class<?>> hookableInterfaces) {
    this.hookableInterfaces = hookableInterfaces;
    for (Hook hook : ServiceLoader.load(Hook.class)) {
      this.addHook(hook);
    }
  }

  private void addHook(Hook hook) {
    Class<? extends Hook> hookClass = hook.getClass();

    if (this.hooks.containsKey(hookClass)) {
      throw new IllegalArgumentException("Hook already registered: " + hookClass.getName());
    }

    HookInfo hookInfo = HookInfo.from(hook, this.hookableInterfaces);
    this.hooks.put(hookClass, hookInfo);
  }

  public <T> T pluginAll(T target) {
    return this.hooks.values().stream().sorted().reduce(target,
        (currentTarget, hookInfo) -> hookInfo.plugin(currentTarget),
        (previousResult, currentResult) -> currentResult);
  }


}
