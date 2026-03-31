package com.evch.rrm.customspring.annotation;

import com.evch.rrm.webserver.CustomHtmlController;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CustomGetMapping {
    String[] value() default "/";
}
