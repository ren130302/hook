package com.ren130302.hook;

public interface Hook {

  Object apply(Invocation invocation) throws Throwable;

}
