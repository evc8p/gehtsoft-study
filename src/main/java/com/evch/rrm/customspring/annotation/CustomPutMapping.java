package com.evch.rrm.customspring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@CustomHttpMethodMapping(method = "PUT")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomPutMapping {
    String httpMethod = "PUT";
    String[] value() default "";
}
