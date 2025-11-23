package com.smartrestaurant.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDto {
    @NotNull(message = "Menu item ID cannot be null")
    private Long dishId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    private BigDecimal priceAtOrder;


}
