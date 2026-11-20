package com.evch.rrm.customspring;

import com.evch.rrm.customspring.contract.ServerRequest;
import com.evch.rrm.customspring.contract.ServerResponse;
import com.evch.rrm.customspring.model.RouteToController;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;

public class RouteHandler {
    @Getter
    private final Map<String, Map<String, RouteToController>> routesToControllers = new HashMap<>();

    public ServerResponse handleRequest(ServerRequest request) {
        String prefix = request.getPrefix();
        try {
            RouteToController routeToController = routesToControllers.get(request.getMethod()).get(request.getPath());
            if (Objects.isNull(routeToController)) {
                routeToController = routesToControllers.get(request.getMethod()).get(prefix);
            }
            if (Objects.isNull(routeToController)) {
                return new ServerResponse(ServerResponse.DataType.ERROR, "500");
            }
            Method method = routeToController.getMethod();
            if (Objects.isNull(method)) {
                return new ServerResponse(ServerResponse.DataType.ERROR, "405");
            }
            Object bean = routeToController.getBean();
            if (method.getParameterCount() == 0) {
                return (ServerResponse) method.invoke(bean, null);
            } else {
                Object[] parameters = new Object[method.getParameterCount()];
                Matcher m = routeToController.getParametersPattern().matcher(prefix);
                if (m.matches()) {
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
                        if ((paramType.isPrimitive() || paramType == String.class)) {
                            String rawValue = methodParams[index++];
                            parameters[i] = Helper.convertType(rawValue, paramType);
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
