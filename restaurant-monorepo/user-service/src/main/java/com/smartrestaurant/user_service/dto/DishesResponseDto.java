package com.smartrestaurant.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DishesResponseDto {
    private Integer id;
    private String name;
    private String description;
    private boolean availability = true;

    @JsonProperty("category")
    private CategoryResponseDto category;
}
