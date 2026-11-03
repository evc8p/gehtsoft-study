package com.evch.rrm.customspring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
public class Controller {
    private Object object;
    private Method method;
    private Pattern parametersPattern;
}
