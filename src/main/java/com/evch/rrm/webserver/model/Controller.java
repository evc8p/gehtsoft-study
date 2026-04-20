package com.evch.rrm.webserver.model;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class Controller {
    private Object object;
    private Method method;

    public Controller(Object object, Method method) {
        this.object = object;
        this.method = method;
    }
}
