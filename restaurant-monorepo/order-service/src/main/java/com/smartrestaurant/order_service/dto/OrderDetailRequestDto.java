package com.smartrestaurant.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

public class OrderDetailRequestDto {
    @NotNull(message = "Menu item ID cannot be null")
    private Long dishId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    private BigDecimal priceAtOrder;

    public OrderDetailRequestDto() {}

    public OrderDetailRequestDto(Long dishId, Integer quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }

    public OrderDetailRequestDto(Long dishId, Integer quantity, BigDecimal priceAtOrder) {
        this.dishId = dishId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }
}
