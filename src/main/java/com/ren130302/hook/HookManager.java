package com.ren130302.hook;

public interface HookManager {

  <T> T applyHooks(T target);


  Object getConfiguration();

}
