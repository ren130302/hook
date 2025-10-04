package com.ren130302.hook;

import java.util.ArrayList;
import java.util.List;

public final class HookManagerFactory {

  private final AllowedInterfaceRegistry allowedInterfaceRegistry = new AllowedInterfaceRegistry();
  private final Hooks hookRegistry = new Hooks();

  public AllowedInterfaceRegistry getAllowedInterfaceRegistry() {
    return this.allowedInterfaceRegistry;
  }

  public Hooks getHookRegistry() {
    return this.hookRegistry;
  }

  public HookManager build(Object config) {
    List<HookDescriptor> hookDefineInfos = new ArrayList<>();

    for (Hook hook : this.hookRegistry.getKnownHooks()) {
      HookDescriptor hookDefineInfo = HookDescriptor.extract(this.allowedInterfaceRegistry, hook);
      hookDefineInfos.add(hookDefineInfo);
    }

    hookDefineInfos.sort(null);

    DefaultHookManager hookManager = new DefaultHookManager(hookDefineInfos, config);

    for (HookDescriptor hookDefineInfo : hookDefineInfos) {
      hookDefineInfo.initHook(hookManager);
    }

    return hookManager;
  }
}
