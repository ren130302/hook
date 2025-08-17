package com.ren130302.hook.core;

import java.util.Collection;
import java.util.Set;
import com.ren130302.hook.api.HookManager;

public final class HookManagerImpl implements HookManager {

  private final Set<HookDefineInfo> hookDefineInfos;

  HookManagerImpl(Collection<HookDefineInfo> collection) {
    this.hookDefineInfos = Set.copyOf(collection);
  }

  @Override
  public <T> T applyHooks(T target) {
    for (HookDefineInfo hookDefineInfo : this.hookDefineInfos) {
      Class<?> targetClass = target.getClass();
      boolean isTargetClass = hookDefineInfo.isTargetClass(targetClass);

      if (isTargetClass) {
        target = hookDefineInfo.bind(target);
      }
    }

    return target;
  }

  public Set<HookDefineInfo> getHookDefineInfos() {
    return this.hookDefineInfos;
  }

}
