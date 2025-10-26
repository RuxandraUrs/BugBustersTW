package com.smartrestaurant.order_service.dto;

import java.math.BigDecimal;

public class OrderDetailResponse {
    private Long dishId;
    private Integer quantity;
    private BigDecimal priceAtOrder;

    public OrderDetailResponse(Long dishId, Integer quantity, BigDecimal priceAtOrder) {
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
