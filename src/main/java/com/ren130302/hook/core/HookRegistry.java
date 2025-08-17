package com.ren130302.hook.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import com.ren130302.hook.api.Hook;

public final class HookRegistry {

  private final Set<Hook> knownHooks = new HashSet<>();

  public HookRegistry add(Hook hook) {
    Objects.requireNonNull(hook, "hook must not be null");
    this.knownHooks.add(hook);
    return this;
  }

  public HookRegistry add(Hook... hooks) {
    Objects.requireNonNull(hooks, "hooks must not be null");
    for (Hook hook : hooks) {
      this.add(hook);
    }
    return this;
  }

  public HookRegistry add(Collection<Hook> hooks) {
    Objects.requireNonNull(hooks, "hooks must not be null");
    hooks.forEach(this::add);
    return this;
  }

  public HookRegistry addFromServiceLoader() {
    for (Hook hook : ServiceLoader.load(Hook.class)) {
      this.add(hook);
    }
    return this;
  }

  public Set<Hook> getKnownHooks() {
    return Set.copyOf(this.knownHooks);
  }

}
