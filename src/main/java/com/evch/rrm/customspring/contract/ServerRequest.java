package com.evch.rrm.customspring.contract;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServerRequest {
    @Getter
    @Setter
    private String method;
    @Getter @Setter
    private String uri;
    @Getter @Setter
    private String version;
    @Getter @Setter
    private Map<String, String> headers;
    @Getter @Setter
    private String body;
    private String source = null;
    private String path = null;
    private String query = null;
    private String methodPattern = null;

    public void reset() {
        method = "";
        uri = "";
        version = "";
        headers = new HashMap<>();
        body = "";
    }

    public String getSource() {
        if (Objects.isNull(source)) {
            source = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
        }
        return source;
    }

    public String getPath() {
        if (Objects.isNull(path)) {
            path = getSource().replaceAll("[^/]+\\.[^/]+$", "");
        }
        return path;
    }

    public String getQuery() {
        if (Objects.isNull(query)) {
            query = uri.contains("?") ? uri.substring(uri.indexOf("?")) : "";
            if (query.length() == 1) {
                query = "";
            }
        }
        return query;
    }

    public String getMethodPattern() {
        if (Objects.isNull(methodPattern)) {
            methodPattern = method + " " + getSource();
        }
        return methodPattern;
    }

    @Override
    public String toString() {
        return "Request{\n" +
                "method='" + method + '\'' + ",\n" +
                "uri='" + uri + '\'' + ",\n" +
                "http version='" + version + '\'' + ",\n" +
                "headers=" + headers + ",\n" +
                "body='" + body + '\'' + "\n" +
                '}';
    }
}
