package com.evch.rrm.webserver;

import com.evch.rrm.customspring.annotation.CustomAutowired;
import com.evch.rrm.customspring.annotation.CustomRestController;
import com.evch.rrm.webserver.model.Controller;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@CustomRestController
public class CustomController {
    @Getter
    @Setter
    @CustomAutowired
    private Map<String, Controller> controllers = new HashMap<>();
}
