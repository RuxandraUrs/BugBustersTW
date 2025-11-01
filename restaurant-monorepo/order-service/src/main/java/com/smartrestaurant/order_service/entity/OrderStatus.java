package com.smartrestaurant.order_service.entity;

public enum OrderStatus {
    PLACED("placed"),
    IN_PROGRESS("in_progress"),
    DELIVERED("delivered"),
    CANCELED("canceled"),
    READY_FOR_PREPARATION("ready_for_preparation");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
