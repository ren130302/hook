package com.ren130302.hook.api;

public interface HookManager {

  <T> T applyHooks(T target);

}
