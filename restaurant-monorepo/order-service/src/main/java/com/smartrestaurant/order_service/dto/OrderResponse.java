package com.smartrestaurant.order_service.dto;

import com.smartrestaurant.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private Long clientId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private List<OrderDetailResponse> items;
    private ZonedDateTime placementDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<OrderDetailResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderDetailResponse> items) {
        this.items = items;
    }

    public ZonedDateTime getPlacementDate() {
        return placementDate;
    }

    public void setPlacementDate(ZonedDateTime placementDate) {
        this.placementDate = placementDate;
    }

    public OrderResponse(Long id, Long clientId, OrderStatus status, BigDecimal totalPrice, String deliveryAddress, List<OrderDetailResponse> items, ZonedDateTime placementDate) {
        this.id = id;
        this.clientId = clientId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
        this.placementDate = placementDate;
    }
}
