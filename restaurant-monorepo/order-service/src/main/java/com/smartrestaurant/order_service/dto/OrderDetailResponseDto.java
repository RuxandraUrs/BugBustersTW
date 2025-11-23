package com.smartrestaurant.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponseDto {
    private Long dishId;
    private Integer quantity;
    private BigDecimal priceAtOrder;

}
