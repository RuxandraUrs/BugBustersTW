package org.example.menuservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class DishDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean availability;
    private Integer categoryId;
    private String categoryName;
    private Set<String> ingredients;
}