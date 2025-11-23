package com.smartrestaurant.order_service.dto;

import com.smartrestaurant.order_service.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private List<OrderDetailResponseDto> items;
    private ZonedDateTime placementDate;

}
