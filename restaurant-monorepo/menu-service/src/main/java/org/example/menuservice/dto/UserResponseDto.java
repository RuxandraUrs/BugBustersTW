package org.example.menuservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String roleName;
    private Float salary;
}