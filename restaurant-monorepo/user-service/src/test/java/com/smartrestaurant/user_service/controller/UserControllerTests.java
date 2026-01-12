package com.smartrestaurant.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartrestaurant.user_service.client.MenuServiceClient;
import com.smartrestaurant.user_service.client.OrderServiceClient;
import com.smartrestaurant.user_service.dto.CategoryResponseDto;
import com.smartrestaurant.user_service.dto.CreateUserDTO;
import com.smartrestaurant.user_service.dto.DishesResponseDto;
import com.smartrestaurant.user_service.dto.UserDTO;
import com.smartrestaurant.user_service.service.IUserService;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Dezactivăm securitatea pentru a testa logica endpoint-ului
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private MenuServiceClient menuServiceClient;

    @MockBean
    private OrderServiceClient orderServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_Success() throws Exception {
        CreateUserDTO createDto = new CreateUserDTO();
        createDto.setName("Tom Harrison");
        createDto.setAddress("100 Wonder Rd");
        createDto.setPhone("tom@test.com");

        UserDTO responseDto = new UserDTO();

        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        UserDTO user1 = new UserDTO();
        user1.setEmail("user1@test.com");
        UserDTO user2 = new UserDTO();
        user2.setEmail("user2@test.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("user1@test.com"));
    }

    @Test
    void updateUser_Success() throws Exception {
        Long userId = 1L;
        UserDTO updateDto = new UserDTO();
        updateDto.setName("John Doe");
        updateDto.setEmail("update@test.com");
        updateDto.setAddress("13 Avenue Street");

        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(updateDto);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void updateUser_InternalServerError() throws Exception {
        UserDTO userDto = new UserDTO();
        when(userService.updateUser(anyLong(), any())).thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Server error ocurred")));
    }

    @Test
    void deleteUser_Success() throws Exception {
        String email = "test@delete.com";
        when(userService.deleteUser(email)).thenReturn(true);

        mockMvc.perform(delete("/api/users/delete/" + email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userService).deleteUser(email);
    }

    @Test
    void deleteUser_NotFound_ReturnsFalse() throws Exception {
        // GIVEN
        String email = "nonexistent@test.com";
        when(userService.deleteUser(email)).thenReturn(false);

        // WHEN & THEN
        mockMvc.perform(delete("/api/users/delete/" + email))
                .andExpect(status().isOk())
                .andExpect(content().string("false")); // Endpoint-ul tău returnează boolean
    }

    @Test
    void getClients_Success() throws Exception {
        // GIVEN
        UserDTO user = new UserDTO();
        user.setEmail("client@test.com");
        when(userService.getClients()).thenReturn(List.of(user));

        // WHEN & THEN
        mockMvc.perform(get("/api/users/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("client@test.com"));
    }

    //intercommunication
    @Test
    void getFilteredDishes_WithParams_Success() throws Exception {
        // GIVEN
        DishesResponseDto dish = new DishesResponseDto();
        dish.setName("Pizza");
        when(menuServiceClient.filterDishes(2, true)).thenReturn(List.of(dish));

        // WHEN & THEN
        mockMvc.perform(get("/api/users/dashboard/dishes/filter")
                        .param("categoryId", "2")
                        .param("availability", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    void getAllCategories_Success() throws Exception {
        // GIVEN
        CategoryResponseDto cat1 = new CategoryResponseDto();
        cat1.setId(1);
        cat1.setName("Pasta");

        CategoryResponseDto cat2 = new CategoryResponseDto();
        cat2.setId(2);
        cat2.setName("Pizza");

        when(menuServiceClient.getAllCategories()).thenReturn(List.of(cat1, cat2));

        // WHEN & THEN
        mockMvc.perform(get("/api/users/dashboard/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Pasta"))
                .andExpect(jsonPath("$[1].name").value("Pizza"));

        verify(menuServiceClient, (VerificationMode) times(1)).getAllCategories();
    }

    @Test
    void getLargeOrders_DefaultValue_Success() throws Exception {
        when(orderServiceClient.getLargeOrders(new BigDecimal("100.00"))).thenReturn(List.of());

        mockMvc.perform(get("/api/users/dashboard/large_orders"))
                .andExpect(status().isOk());

        verify(orderServiceClient).getLargeOrders(new BigDecimal("100.00"));
    }

}
