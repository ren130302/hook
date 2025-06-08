package com.ren130302.hook.api;

public interface Hook {

  Object apply(Invocation invocation) throws Throwable;

}
