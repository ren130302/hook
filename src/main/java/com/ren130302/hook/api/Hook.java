package com.ren130302.hook.api;

public interface Hook {

  default void init(HookManager hookManager) {}

  Object apply(Invocation invocation) throws Throwable;

}
