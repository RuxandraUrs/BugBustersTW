package org.example.menuservice.service;

import lombok.RequiredArgsConstructor;
import org.example.menuservice.client.OrderServiceClient;
import org.example.menuservice.client.UserServiceClient;
import org.example.menuservice.dto.OrderResponseDto;
import org.example.menuservice.dto.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderServiceClient orderServiceClient;
    private final UserServiceClient userServiceClient;

    public List<OrderResponseDto> getAllOrdersForAdmin() {
        return orderServiceClient.getAllOrders();
    }

    public List<OrderResponseDto> getOrdersForClient(String clientName) {
        return orderServiceClient.getOrdersByClient(clientName);
    }

    public List<UserResponseDto> getAllClients() {
        return userServiceClient.getClients();
    }

    public Long getEmployeeCount() {
        return userServiceClient.countEmployees();
    }
}