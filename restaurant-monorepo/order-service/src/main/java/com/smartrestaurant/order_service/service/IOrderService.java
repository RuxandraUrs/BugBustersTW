package com.smartrestaurant.order_service.service;

import com.smartrestaurant.order_service.dto.OrderDetailedResponseDto;
import com.smartrestaurant.order_service.dto.OrderRequestDto;
import com.smartrestaurant.order_service.dto.OrderResponseDto;
import com.smartrestaurant.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {

 OrderResponseDto createOrder(OrderRequestDto request); // CREATE
 OrderResponseDto getOrderById(Long orderId); // READ
    List<OrderResponseDto> getAllOrders(); // READ ALL
    OrderResponseDto updateOrder(Long orderId, OrderRequestDto updateRequest); //UPDATE
    OrderResponseDto deleteOrder(Long id); //DELETE


    //Extra endpoints
    OrderResponseDto updateStatusReadyForPreparation(Long id);
    List<OrderResponseDto> getOrdersByStatus(OrderStatus status);
    List<OrderResponseDto> getLargeOrdersSortedByTotal(BigDecimal minTotal);

    //Extra endpoints for services intercommunication
    OrderDetailedResponseDto getOrderDetails(Long id);
    List<OrderResponseDto> getOrdersByClientName(String name);
    List<OrderResponseDto> getOrdersByDishName(String dishName);









}
