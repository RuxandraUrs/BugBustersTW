package com.smartrestaurant.order_service.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String role;
    private String name;
}
