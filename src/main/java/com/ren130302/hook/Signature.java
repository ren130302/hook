package com.ren130302.hook;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Signature {

  String name();

  Class<?>[] paramTypes();

  Class<?> type();

}
