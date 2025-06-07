package com.ren130302.hook;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HookDefine {

  public static final class Priority {

    public static final byte HIGH = 40;
    public static final byte HIGHER = 20;
    public static final byte HIGHEST = 0;
    public static final byte LOW = 80;
    public static final byte LOWER = 100;
    public static final byte LOWEST = 120;
    public static final byte MEDIUM = 60;

  }

  byte priority() default Priority.LOWEST;

  Signature[] value();

}
