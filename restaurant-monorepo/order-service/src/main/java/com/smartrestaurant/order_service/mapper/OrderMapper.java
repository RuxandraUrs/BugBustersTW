package com.smartrestaurant.order_service.mapper;

import com.smartrestaurant.order_service.dto.OrderDetailResponseDto;
import com.smartrestaurant.order_service.dto.OrderRequestDto;
import com.smartrestaurant.order_service.dto.OrderResponseDto;
import com.smartrestaurant.order_service.entity.Order;
import com.smartrestaurant.order_service.entity.OrderDetail;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderResponseDto toResponseDto(Order order){
        if(order == null) return null;

        List<OrderDetailResponseDto> detailResponses = order.getOrderDetails().stream()
                .map(OrderMapper::toDetailResponseDto)
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getClientId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getDeliveryAddress(),
                detailResponses,
                order.getPlacementDate()
        );
    }

    public static OrderDetailResponseDto toDetailResponseDto(OrderDetail detail){
        if(detail == null) return null;

        return new OrderDetailResponseDto(
                detail.getDishId(),
                detail.getQuantity(),
                detail.getPriceAtOrder()
        );
    }

    public static Order toEntity(OrderRequestDto request){
        if(request == null) return null;

        Order order = new Order();
        order.setClientId(request.getClientId());
        order.setDeliveryAddress(request.getDeliveryAddress());

        return order;
    }
}
