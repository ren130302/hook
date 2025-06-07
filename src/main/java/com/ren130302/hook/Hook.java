package com.ren130302.hook;

public interface Hook {

  Object intercept(Invocation invocation) throws Throwable;

}
