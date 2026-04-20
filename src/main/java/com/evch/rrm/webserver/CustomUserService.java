package com.evch.rrm.webserver;

import com.evch.rrm.ConcurrentHashMap;
import com.evch.rrm.customspring.annotation.CustomAutowired;
import com.evch.rrm.customspring.annotation.CustomService;
import com.evch.rrm.webserver.dao.CustomUserRepository;
import com.evch.rrm.webserver.dto.CustomUserDto;
import com.evch.rrm.webserver.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@CustomService
public class CustomUserService {
    private Map<Long, User> users = new ConcurrentHashMap<>(); // In-memory storage
    private AtomicLong idGenerator = new AtomicLong(1);
    private CustomUserRepository userRepository;

    @CustomAutowired
    public CustomUserService(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<CustomUserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        List<CustomUserDto> usersDto = new ArrayList<>();
        users.forEach(user -> usersDto.add(new CustomUserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress())));
        return usersDto;
    }

    public CustomUserDto getUserById(Long id) {
        User user = userRepository.getUserById(id);
        return new CustomUserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress());
    }

    public CustomUserDto createUser(CustomUserDto dto) {
        userRepository.createUser(new User(dto.getId(), dto.getName(), dto.getEmail(), dto.getAddress()));
        return dto;
    }

    public CustomUserDto updateUser(Long id, CustomUserDto dto) {
        userRepository.updateUser(id, new User(dto.getId(), dto.getName(), dto.getEmail(), dto.getAddress()));
        return dto;
    }

    public CustomUserDto patchUser(Long id, Map<String, Object> updates) {
        User user = userRepository.patchUser(id, updates);
        return new CustomUserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress());
    }

    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}
