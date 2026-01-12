package com.smartrestaurant.user_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailResponseDto {
    private String dishName;
    private Integer quantity;
    private BigDecimal price;
}
