package org.example.menuservice.controller;

import org.example.menuservice.dto.OrderResponseDto;
import org.example.menuservice.dto.UserResponseDto;
import org.example.menuservice.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void getAllOrders_ReturnsOk() throws Exception {
        when(dashboardService.getAllOrdersForAdmin())
                .thenReturn(Collections.singletonList(new OrderResponseDto()));

        mockMvc.perform(get("/api/menu/dashboard/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void getClientOrders_ReturnsOk() throws Exception {
        String name = "Diana";
        when(dashboardService.getOrdersForClient(name))
                .thenReturn(Collections.singletonList(new OrderResponseDto()));

        mockMvc.perform(get("/api/menu/dashboard/my-orders").param("name", name))
                .andExpect(status().isOk());
    }

    @Test
    void searchUsers_ReturnsOk() throws Exception {
        String name = "Diana";
        when(dashboardService.searchUsers(name))
                .thenReturn(Collections.singletonList(new UserResponseDto()));

        mockMvc.perform(get("/api/menu/dashboard/users/search").param("name", name))
                .andExpect(status().isOk());
    }

    @Test
    void getEmployeeCount_ReturnsOk() throws Exception {
        when(dashboardService.getEmployeeCount()).thenReturn(10L);

        mockMvc.perform(get("/api/menu/dashboard/users/employees-count"))
                .andExpect(status().isOk());
    }
}