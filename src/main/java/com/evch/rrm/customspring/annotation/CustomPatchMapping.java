package com.evch.rrm.customspring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomHttpMethodMapping(httpMethod = "PATCH")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomPatchMapping {
    String[] value() default "";
}
