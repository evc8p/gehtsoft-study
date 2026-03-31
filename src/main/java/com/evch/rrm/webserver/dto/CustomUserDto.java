package com.evch.rrm.webserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomUserDto {
    private Long id;
    private String name;
    private String email;
    private String address;
}
