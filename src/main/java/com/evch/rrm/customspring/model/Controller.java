package com.evch.rrm.customspring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class Controller {
    private Object bean;
    private Method method;
    private Pattern parametersPattern;
    private Type[] type;
    private boolean isStaticContent;
}
