package org.example.menuservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.menuservice.dto.OrderResponseDto;
import org.example.menuservice.dto.UserResponseDto;
import org.example.menuservice.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    //  http://localhost:8080/api/menu/dashboard/orders
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(dashboardService.getAllOrdersForAdmin());
    }

    //  http://localhost:8080/api/menu/dashboard/my-orders?name=Diana
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponseDto>> getClientOrders(@RequestParam String name) {
        return ResponseEntity.ok(dashboardService.getOrdersForClient(name));
    }

    // http://localhost:8080/api/menu/dashboard/users/search?name=Diana
    @GetMapping("/users/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(dashboardService.searchUsers(name));
    }

    // http://localhost:8080/api/menu/dashboard/users/employees-count
    @GetMapping("/users/employees-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getEmployeeCount() {
        return ResponseEntity.ok(dashboardService.getEmployeeCount());
    }
}