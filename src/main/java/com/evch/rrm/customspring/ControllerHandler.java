package com.evch.rrm.customspring;

import com.evch.rrm.customspring.contract.ServerRequest;
import com.evch.rrm.customspring.contract.ServerResponse;
import com.evch.rrm.customspring.model.Controller;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class ControllerHandler {
    @Getter
    private final Map<String, Controller> controllers = new HashMap<>();

    public ServerResponse handleRequest(ServerRequest request) {
        try {
            Controller controllerMethod = controllers.get(request.getMethod() + " " + request.getPath());
            Method method = controllerMethod.getMethod();
            Object bean = controllerMethod.getBean();
            if (method.getParameterCount() == 0) {
                return (ServerResponse) method.invoke(bean, null);
            } else {
                Matcher m = controllerMethod.getParametersPattern().matcher(request.getMethodPattern());
                String[] methodParams = new String[m.groupCount()];
                if (m.matches()) {
                    for (int i = 0; i < methodParams.length; i++) {
                        methodParams[i] = m.group(i + 1);
                    }
                }
                if (methodParams.length == 0) {
                    return (ServerResponse) method.invoke(bean, request);
                } else {
                    return (ServerResponse) method.invoke(bean, methodParams);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException |
                 NullPointerException | ExceptionInInitializerError e) {
            e.printStackTrace();
        }
        return null;
    }
}
