package com.evch.rrm.customspring;

import com.evch.rrm.customspring.contract.ServerRequest;
import com.evch.rrm.customspring.contract.ServerResponse;
import com.evch.rrm.customspring.model.Controller;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
            boolean isStaticContent = controllerMethod.isStaticContent();
            if (method.getParameterCount() == 0) {
                return (ServerResponse) method.invoke(bean, null);
            } else {
                Object[] parameters = new Object[method.getParameterCount()];
                Matcher m = controllerMethod.getParametersPattern().matcher(request.getMethodPattern());
                if (m.matches() || isStaticContent) {
                    String[] methodParams = new String[m.groupCount()];
                    for (int j = 0; j < methodParams.length; j++) {
                        methodParams[j] = m.group(j + 1);
                    }
                    int index = 0;
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        Parameter parameter = method.getParameters()[i];
                        if (ServerRequest.class.isAssignableFrom(parameter.getType())) {
                            parameters[i] = request;
                            continue;
                        }
                        Class<?> paramType = parameter.getType();
                        if ((paramType.isPrimitive() || paramType == String.class) && !isStaticContent) {
                            String rawValue = methodParams[index++];
                            parameters[i] = Helper.convert(rawValue, paramType);
                        } else {
                            return new ServerResponse(ServerResponse.DataType.ERROR, "404");
                        }
                    }
                    return (ServerResponse) method.invoke(bean, parameters);
                }
                return new ServerResponse(ServerResponse.DataType.ERROR, "404"); // the data format is incorrect
            }
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException |
                 NullPointerException | ExceptionInInitializerError e) {
            e.printStackTrace();
        }
        return null;
    }
}
