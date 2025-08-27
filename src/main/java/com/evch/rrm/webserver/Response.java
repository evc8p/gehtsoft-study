package com.evch.rrm.webserver;

import lombok.Getter;
import lombok.Setter;

public class Response {
    @Getter
    @Setter
    private DataType dataType;
    @Getter
    @Setter
    private String data;

    public Response() {
        this.dataType = DataType.TEXT;
        this.data = "{}";
    }

    public Response(DataType dataType, String data) {
        this.dataType = dataType;
        this.data = data;
    }

    public enum DataType {
        TEXT,
        JSON,
        FILE
    }
}
