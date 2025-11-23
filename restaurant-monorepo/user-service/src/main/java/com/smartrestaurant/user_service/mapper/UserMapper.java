package com.smartrestaurant.user_service.mapper;

import com.smartrestaurant.user_service.dto.CreateUserDTO;
import com.smartrestaurant.user_service.dto.UserDTO;
import com.smartrestaurant.user_service.entity.User;

public class UserMapper {
    //dto to entity
    public static User toEntity(CreateUserDTO userDTO)
    {
        return new User(
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getAddress(),
                userDTO.getPhone(),
                userDTO.getRoleName(),
                userDTO.getSalary()
        );
    }

    //enitity to dto
    public static UserDTO toDTO(User user)
    {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone(),
                user.getRole(),
                user.getSalary()
        );
    }


}
