package com.ren130302.hook;

import java.util.Collection;
import java.util.List;

public final class DefaultHookManager implements HookManager {

  private final List<HookDescriptor> hookDefineInfos;
  private final Object config;

  DefaultHookManager(Collection<HookDescriptor> collection, Object config) {
    this.hookDefineInfos = List.copyOf(collection);
    this.config = config;
  }

  @Override
  public <T> T applyHooks(T target) {
    for (HookDescriptor hookDefineInfo : this.hookDefineInfos) {
      Class<?> targetClass = target.getClass();
      boolean isTargetClass = hookDefineInfo.isTargetClass(targetClass);

      if (isTargetClass) {
        target = hookDefineInfo.bind(target);
      }
    }

    return target;
  }

  public List<HookDescriptor> getHookDefineInfos() {
    return this.hookDefineInfos;
  }

  @Override
  public Object getConfiguration() {
    return this.config;
  }

}
