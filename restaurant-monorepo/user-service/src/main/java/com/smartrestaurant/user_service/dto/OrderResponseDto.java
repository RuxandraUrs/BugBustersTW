package com.smartrestaurant.user_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private Long employeeId;
    private ZonedDateTime placementDate;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private List<OrderDetailResponseDto> orderDetails = new ArrayList<>();
}
