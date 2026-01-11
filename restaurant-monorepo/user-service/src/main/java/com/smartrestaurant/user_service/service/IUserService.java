package com.smartrestaurant.user_service.service;

import com.smartrestaurant.user_service.dto.CreateUserDTO;
import com.smartrestaurant.user_service.dto.UserDTO;
import com.smartrestaurant.user_service.entity.User;

import java.util.List;

public interface IUserService {
    //CRUD operations
    UserDTO createUser(CreateUserDTO createUserDTO); //Create

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers(); //Read
    UserDTO updateUser(Long id, UserDTO userDTO); //Update
    boolean deleteUser(String email);  //Delete

    //Extra endpoints
    List<UserDTO> getClients();
    Long countEmployees();
    List<UserDTO> findByName(String name);
}
