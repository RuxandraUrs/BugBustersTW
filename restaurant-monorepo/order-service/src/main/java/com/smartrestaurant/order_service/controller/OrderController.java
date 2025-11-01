package com.smartrestaurant.order_service.controller;

import com.smartrestaurant.order_service.dto.OrderRequestDto;
import com.smartrestaurant.order_service.dto.OrderResponseDto;
import com.smartrestaurant.order_service.service.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid  @RequestBody OrderRequestDto request) {
        OrderResponseDto response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody OrderRequestDto request) {
        try {
            OrderResponseDto response = orderService.updateOrder(id, request); // ← Schimbă aici
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status_ready")
    public ResponseEntity<OrderResponseDto> markOrderReady(@PathVariable Long id) {
        try {
            OrderResponseDto response = orderService.updateStatusReadyForPreparation(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<OrderResponseDto> deleteOrder(@PathVariable Long id) {
        try {
            OrderResponseDto response = orderService.deleteOrder(id);
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }










}
