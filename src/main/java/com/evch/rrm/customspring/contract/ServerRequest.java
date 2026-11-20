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
    @Getter @Setter
    private String path = null;
    private String prefix = null;
    @Getter @Setter
    private String query = null;

    public void reset() {
        method = "";
        uri = "";
        version = "";
        headers = new HashMap<>();
        body = "";
    }

    public String getPath() {
        if (Objects.isNull(path)) {
            path = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
        }
        return path;
    }

    public String getPrefix() {
        if (Objects.isNull(prefix)) {
            prefix = getPath().replaceAll("[^/]+\\.[^/]+$", "");
            if (prefix.length() > 1 && prefix.endsWith("/")) prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix;
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

    @Override
    public String toString() {
        return "ServerRequest{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                ", path='" + path + '\'' +
                ", prefix='" + prefix + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
