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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder =new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public UserDTO getUserById(Long id) {
        User user= userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with this id does not exists!"));
        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

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

    @Override
    public boolean deleteUser(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User with this email does not exist!"));

        userRepository.delete(user);

        return userRepository.findByEmail(email).isEmpty();
    }

    @Override
    public List<UserDTO> getClients() {
        List<User> clients = userRepository.findByRole((Role.CLIENT));

        return clients.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countEmployees() {
        return userRepository.countByRole(Role.EMPLOYEE);
    }

    @Override
    public List<UserDTO> findByName(String name) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
