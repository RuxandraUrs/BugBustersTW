package com.smartrestaurant.user_service.service;

import com.smartrestaurant.user_service.dto.CreateUserDTO;
import com.smartrestaurant.user_service.dto.UserDTO;
import com.smartrestaurant.user_service.entity.User;
import com.smartrestaurant.user_service.enums.Role;
import com.smartrestaurant.user_service.mapper.UserMapper;
import com.smartrestaurant.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing USERS in the Restaurant system.
 * This class handles business logic for CRUD operations, role-based filtering and secure password management.
 * It communicates with Order and Menu Services for data aggregation.
 * 
 *  @author [Huruba Adriana]
 *  @version 1.0
 */

@Service
public class UserServiceImpl implements IUserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder =new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    /**
     * Registers a new user in the system
     * Validates if email is unique and encrypts the password before persistence.
     *
     * @param createUserDTO Data transfer object containing new user details(name, email, password etc.)
     * @return UserDTO containing the new user created with its information.
     * @throws RuntimeException if a user with the provided email already exists.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public UserDTO createUser(CreateUserDTO createUserDTO)
    {
        if(userRepository.findByEmail(createUserDTO.getEmail()).isPresent())
        {
            throw new RuntimeException("User with this email already exists!");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(createUserDTO.getPassword()); //hashingul parolei

        User user = UserMapper.toEntity(createUserDTO);
        user.setPassword_hash(hashedPassword);
        user.setName(createUserDTO.getName());
        user.setEmail(createUserDTO.getEmail());
        user.setPhone(createUserDTO.getPhone());
        user.setAddress(createUserDTO.getAddress());
        user.setRole(createUserDTO.getRoleName());
        User savedUser = userRepository.save(user);

        return UserMapper.toDTO(savedUser);
    }

    /**
     * Retrieves a specific user by their unique identifier.
     *
     * @param id The ID of the user to retrieve.
     * @return UserDTO if the user is found.
     * @throws RuntimeException if no user exists with the given ID.
     *
     *  @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public UserDTO getUserById(Long id) {
        User user= userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with this id does not exists!"));
        return UserMapper.toDTO(user);
    }

    /**
     * Returns all registered users in the database.
     *
     * @return A list of UserDTO objects.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user's information.
     *
     * @param id The ID of the user to update.
     * @param userDTO The updated data to be applied.
     * @return UserDTO containing the updated details.
     * @throws RuntimeException if the user is not found.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setRole(userDTO.getRoleName());
        existingUser.setSalary(userDTO.getSalary());

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDTO(updatedUser);
    }

    /**
     * Deletes a user from the system based on their email address.
     *
     * @param email The email of the user to be removed.
     * @return true if the deletion was successful and verified.
     * @throws RuntimeException if the email does not match with any existing user's email.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public boolean deleteUser(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User with this email does not exist!"));

        userRepository.delete(user);

        return userRepository.findByEmail(email).isEmpty();
    }

    /**
     * Returns a list of all the existing clients.
     *
     * @return all the users with role CLIENT as UserDTO objects.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public List<UserDTO> getClients() {
        List<User> clients = userRepository.findByRole((Role.CLIENT));

        return clients.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Counts the total number of employees currently in the system.
     *
     * @return total count of users with the EMPLOYEE role.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public Long countEmployees() {
        return userRepository.countByRole(Role.EMPLOYEE);
    }

    /**
     * Searches for users whose name contains the specified search string.
     * The search is case-insensitive.
     *
     * @param name The substring to search for within user names.
     * @return A list of matching users as UserDTO objects.
     *
     * @author [Huruba Adriana] - [12.01.2026]
     */
    @Override
    public List<UserDTO> findByName(String name) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
