package com.smartrestaurant.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull private Long clientId;
    @NotBlank @Size(max = 200) private String  deliveryAddress;
    @NotEmpty private List<OrderDetailRequestDto>  items;

}
