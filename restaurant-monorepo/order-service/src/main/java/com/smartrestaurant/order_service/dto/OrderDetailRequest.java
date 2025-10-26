package com.smartrestaurant.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderDetailRequest {
    @NotNull(message = "Menu item ID cannot be null")
    private Long dishId;

    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    public OrderDetailRequest() {}

    public OrderDetailRequest(Long dishId, Integer quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
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
}
