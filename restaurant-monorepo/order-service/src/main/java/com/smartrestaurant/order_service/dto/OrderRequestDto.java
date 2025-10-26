package com.smartrestaurant.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OrderRequestDto {
    @NotNull private Long clientId;
    @NotBlank @Size(max = 200) private String  deliveryAddress;
    @NotEmpty private List<OrderDetailRequestDto>  items;

    public OrderRequestDto() {}

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<OrderDetailRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderDetailRequestDto> items) {
        this.items = items;
    }
}
