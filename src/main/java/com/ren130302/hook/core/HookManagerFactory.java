package com.ren130302.hook.core;

import java.util.HashSet;
import java.util.Set;
import com.ren130302.hook.api.Hook;
import com.ren130302.hook.api.HookManager;

public final class HookManagerFactory {

  private final AllowedInterfaceRegistry allowedInterfaceRegistry = new AllowedInterfaceRegistry();
  private final HookRegistry hookRegistry = new HookRegistry();

  public AllowedInterfaceRegistry getAllowedInterfaceRegistry() {
    return this.allowedInterfaceRegistry;
  }

  public HookRegistry getHookRegistry() {
    return this.hookRegistry;
  }

  public HookManager build() {
    Set<HookDefineInfo> hookDefineInfos = new HashSet<>();

    for (Hook hook : this.hookRegistry.getKnownHooks()) {
      HookDefineInfo hookDefineInfo = HookDefineInfo.extract(this.allowedInterfaceRegistry, hook);
      hookDefineInfos.add(hookDefineInfo);
    }

    HookManagerImpl hookManager = new HookManagerImpl(hookDefineInfos);

    for (HookDefineInfo hookDefineInfo : hookManager.getHookDefineInfos()) {
      hookDefineInfo.initHook(hookManager);
    }

    return hookManager;
  }
}
