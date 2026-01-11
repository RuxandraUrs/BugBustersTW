package com.smartrestaurant.order_service.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishResponseDto {
    private Integer id;
    private String name;
    private BigDecimal price;
}
