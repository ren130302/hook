package com.ren130302.hook;

public class HookHandlerException extends RuntimeException {


  private static final long serialVersionUID = 8011915683212240120L;

  public HookHandlerException(String message) {
    super(message);
  }


  public HookHandlerException(String message, Throwable cause) {
    super(message, cause);
  }

  public HookHandlerException(Throwable cause) {
    super(cause);
  }
}
