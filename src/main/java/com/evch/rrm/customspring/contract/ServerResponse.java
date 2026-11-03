package com.evch.rrm.customspring.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerResponse {
    private DataType dataType;
    private String data;

    public ServerResponse() {
        this.dataType = DataType.TEXT;
        this.data = "{}";
    }

    public ServerResponse(DataType dataType, String data) {
        this.dataType = dataType;
        this.data = data;
    }

    public enum DataType {
        TEXT,
        JSON,
        FILE,
        ERROR
    }
}
