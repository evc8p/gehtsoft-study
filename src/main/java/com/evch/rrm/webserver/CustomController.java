package com.evch.rrm.webserver;

import com.evch.rrm.customspring.ControllerHandler;
import com.evch.rrm.customspring.annotation.CustomAutowired;
import com.evch.rrm.customspring.annotation.CustomRestController;
import lombok.Getter;
import lombok.Setter;

@CustomRestController
public class CustomController {
    @Getter
    @Setter
    @CustomAutowired
    private ControllerHandler controllerHandler;
}
