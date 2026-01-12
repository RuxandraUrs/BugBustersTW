package com.smartrestaurant.user_service.service;

import com.smartrestaurant.user_service.dto.CreateUserDTO;
import com.smartrestaurant.user_service.dto.UserDTO;
import com.smartrestaurant.user_service.entity.User;
import com.smartrestaurant.user_service.enums.Role;
import com.smartrestaurant.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_Success() {
        // GIVEN
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("new@test.com");
        dto.setPassword("password123");
        dto.setName("Test User");
        dto.setRoleName(Role.CLIENT);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN
        UserDTO result = userService.createUser(dto);

        // THEN
        assertNotNull(result);
        assertEquals("new@test.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ThrowsException_WhenEmailExists() {
        // GIVEN
        CreateUserDTO dto = new CreateUserDTO();
        dto.setEmail("existing@test.com");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(new User()));

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(dto);
        });

        assertEquals("User with this email already exists!", exception.getMessage());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_ReturnsEmptyList() {
        // GIVEN
        when(userRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<UserDTO> result = userService.getAllUsers();

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void updateUser_Success() {
        // GIVEN
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setRole(Role.CLIENT);

        UserDTO updateDto = new UserDTO();
        updateDto.setName("New Name");
        updateDto.setEmail("new@email.com");
        updateDto.setRoleName(Role.EMPLOYEE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN
        UserDTO result = userService.updateUser(userId, updateDto);

        // THEN
        assertEquals("New Name", result.getName());
        assertEquals("EMPLOYEE", result.getRoleName().toString());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, new UserDTO()));
    }

    @Test
    void deleteUser_Success() {
        // GIVEN
        String email = "delete@test.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user), Optional.empty());

        // WHEN
        boolean result = userService.deleteUser(email);

        // THEN
        assertTrue(result);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // GIVEN
        String email = "notfound@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(email));
        assertEquals("User with this email does not exist!", exception.getMessage());
    }
}
