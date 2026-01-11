package com.smartrestaurant.order_service.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailedResponseDto {
    private Long orderId;
    private String clientEmail;
    private BigDecimal total;
    private String address;
    private List<String> dishNames;
}