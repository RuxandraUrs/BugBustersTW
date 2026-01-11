package org.example.menuservice.service;

import org.example.menuservice.client.OrderServiceClient;
import org.example.menuservice.client.UserServiceClient;
import org.example.menuservice.dto.OrderResponseDto;
import org.example.menuservice.dto.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private OrderServiceClient orderServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getAllOrdersForAdmin_Success() {
        List<OrderResponseDto> mockOrders = Collections.singletonList(new OrderResponseDto());
        when(orderServiceClient.getAllOrders()).thenReturn(mockOrders);

        List<OrderResponseDto> result = dashboardService.getAllOrdersForAdmin();

        assertEquals(1, result.size());
        verify(orderServiceClient).getAllOrders();
    }

    @Test
    void getOrdersForClient_Success() {
        String clientName = "Diana";
        List<OrderResponseDto> mockOrders = Collections.singletonList(new OrderResponseDto());
        when(orderServiceClient.getOrdersByClient(clientName)).thenReturn(mockOrders);

        List<OrderResponseDto> result = dashboardService.getOrdersForClient(clientName);

        assertEquals(1, result.size());
        verify(orderServiceClient).getOrdersByClient(clientName);
    }

    @Test
    void searchUsers_Success() {
        String query = "Diana";
        List<UserResponseDto> mockUsers = Collections.singletonList(new UserResponseDto());
        when(userServiceClient.searchUsers(query)).thenReturn(mockUsers);

        List<UserResponseDto> result = dashboardService.searchUsers(query);

        assertEquals(1, result.size());
        verify(userServiceClient).searchUsers(query);
    }

    @Test
    void getEmployeeCount_Success() {
        Long expectedCount = 5L;
        when(userServiceClient.countEmployees()).thenReturn(expectedCount);

        Long result = dashboardService.getEmployeeCount();

        assertEquals(expectedCount, result);
        verify(userServiceClient).countEmployees();
    }
}