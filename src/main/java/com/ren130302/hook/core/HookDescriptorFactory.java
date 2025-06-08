package com.ren130302.hook.core;

import java.util.Objects;
import com.ren130302.hook.api.Hook;
import com.ren130302.hook.api.HookDefine;
import com.ren130302.hook.api.Signature;

final class HookDescriptorFactory {

  private HookDescriptorFactory() {}

  public static HookDescriptor create(HookManager hookManager, Hook hook) {
    Objects.requireNonNull(hookManager, "hookManager must not be null");
    Objects.requireNonNull(hook, "hook must not be null");

    MethodRegistry methodRegistry = new MethodRegistry(hookManager);
    HookDefine hookDefine = extractHookDefine(hook);

    for (Signature signature : hookDefine.value()) {
      methodRegistry.resolve(signature);
    }

    return new HookDescriptor(hook, methodRegistry);
  }

  private static HookDefine extractHookDefine(Hook hook) {
    HookDefine hookDefine = hook.getClass().getAnnotation(HookDefine.class);
    if (hookDefine == null) {
      throw new IllegalStateException(
          "Could not find @HookDefine annotation: " + hook.getClass().getName());
    }
    return hookDefine;
  }
}
