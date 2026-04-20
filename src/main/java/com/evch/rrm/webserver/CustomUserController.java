package com.evch.rrm.webserver;

import com.evch.rrm.customspring.annotation.*;
import com.evch.rrm.webserver.dto.CustomUserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.evch.rrm.webserver.Response.DataType.TEXT;

@CustomRequestMapping("/api/v1/users")
public class CustomUserController extends CustomController {
    private final CustomUserService userService;

    @CustomAutowired
    public CustomUserController(CustomUserService userService) {
        this.userService = userService;
    }

    @CustomGetMapping
    public Response getAllUsers() {
        List<CustomUserDto> users = userService.getAllUsers();
        List<String> usersNames = new ArrayList<>();
        users.forEach(userDto -> usersNames.add(userDto.getName()));
        return new Response(TEXT, usersNames.toString());
    }

    @CustomGetMapping("/{id}")
    public Response getUserById(@CustomPathVariable("id") Long id) {
        CustomUserDto user = userService.getUserById(id);
        return new Response(TEXT, "user with id = " + id + ": " + user.getName());
    }

    @CustomPostMapping("/create")
    public Response createUser(@CustomRequestBody CustomUserDto user) {
        CustomUserDto newUser = userService.createUser(user);
        return new Response(TEXT, "user with id = " + newUser.getId() + " created: " + newUser.getName());
    }

    @CustomPutMapping("/update/{id}")
    public Response updateUser(@CustomPathVariable("id") Long id, @CustomRequestBody CustomUserDto user) {
        CustomUserDto updatedUser = userService.updateUser(id, user);
        return new Response(TEXT, "user with id = " + updatedUser.getId() + " updated: " + updatedUser.getName());
    }

    @CustomPatchMapping("/patch/{id}")
    public Response patchUser(@CustomPathVariable("id") Long id, @CustomRequestBody Map<String, Object> updates) {
        CustomUserDto userDto = userService.patchUser(id, updates);
        return new Response(TEXT, "user with id = " + id + " patched: " + userDto.getName());
    }

    @CustomDeleteMapping("delete/{id}")
    public Response deleteUser(@CustomPathVariable("id") Long id) {
        userService.deleteUser(id);
        return new Response(TEXT, "user with id = " + id + " deleted: " + id);
    }
}
