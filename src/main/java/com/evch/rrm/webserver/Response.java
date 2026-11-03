package com.evch.rrm.webserver;

import com.evch.rrm.customspring.contract.ServerResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response extends ServerResponse {

    public Response() {
        super();
    }

    public Response(ServerResponse.DataType dataType, String data) {
        super(dataType, data);
    }
}
