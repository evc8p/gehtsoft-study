package com.evch.rrm.webserver;

import com.evch.rrm.customspring.annotation.*;
import com.evch.rrm.webserver.dto.CustomUserDto;

import java.util.List;
import java.util.Map;

@CustomRequestMapping("/api/v1/users")
public class CustomUserController extends CustomController {
    private final CustomUserService userService;

    @CustomAutowired
    public CustomUserController(CustomUserService userService) {
        this.userService = userService;
    }

    @CustomGetMapping
    public List<CustomUserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @CustomGetMapping("/{id}")
    public CustomUserDto getUserById(@CustomPathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @CustomPostMapping
    public CustomUserDto createUser(@CustomRequestBody CustomUserDto user) {
        return userService.createUser(user);
    }

    @CustomPutMapping("/{id}")
    public CustomUserDto updateUser(@CustomPathVariable("id") Long id, @CustomRequestBody CustomUserDto user) {
        return userService.updateUser(id, user);
    }

    @CustomPatchMapping("/{id}")
    public CustomUserDto patchUser(@CustomPathVariable("id") Long id, @CustomRequestBody Map<String, Object> updates) {
        return userService.patchUser(id, updates);
    }

    @CustomDeleteMapping("/{id}")
    public void deleteUser(@CustomPathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
