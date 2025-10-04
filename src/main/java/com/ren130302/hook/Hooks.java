package com.ren130302.hook;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;

public final class Hooks {

  private final Set<Class<? extends Hook>> knownHookClasses = new HashSet<>();
  private final Set<Hook> knownHooks = new HashSet<>();

  public Hooks addClass(Class<? extends Hook> hookClass) {
    Objects.requireNonNull(hookClass, "hookClass must not be null");

    if (this.knownHookClasses.add(hookClass)) {
      try {
        this.knownHooks.add(hookClass.getDeclaredConstructor().newInstance());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        throw new IllegalArgumentException(e);
      }
    }

    return this;
  }

  @SuppressWarnings("unchecked")
  public Hooks addClasses(Class<? extends Hook>... hookClasses) {
    Objects.requireNonNull(hookClasses, "hookClasses must not be null");

    for (Class<? extends Hook> hookClass : hookClasses) {
      this.addClass(hookClass);
    }

    return this;
  }

  public Hooks addClasses(Collection<Class<? extends Hook>> hookClasses) {
    Objects.requireNonNull(hookClasses, "hookClasses must not be null");
    hookClasses.forEach(this::addClass);
    return this;
  }

  public Hooks addHook(Hook hook) {
    Objects.requireNonNull(hook, "hook must not be null");

    Class<? extends Hook> hookClass = hook.getClass();

    if (this.knownHookClasses.add(hookClass)) {
      this.knownHooks.add(hook);
    }

    return this;
  }

  public Hooks addHooks(Hook... hooks) {
    Objects.requireNonNull(hooks, "hooks must not be null");
    for (Hook hook : hooks) {
      this.addHook(hook);
    }
    return this;
  }

  public Hooks addHooks(Collection<Hook> hooks) {
    Objects.requireNonNull(hooks, "hooks must not be null");
    hooks.forEach(this::addHook);
    return this;
  }

  public Hooks addFromServiceLoader() {
    for (Hook hook : ServiceLoader.load(Hook.class)) {
      this.addHook(hook);
    }
    return this;
  }

  public Set<Hook> getKnownHooks() {
    return Set.copyOf(this.knownHooks);
  }

}
