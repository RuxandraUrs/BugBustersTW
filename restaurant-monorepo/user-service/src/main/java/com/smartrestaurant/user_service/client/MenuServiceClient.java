package com.smartrestaurant.user_service.client;

import com.smartrestaurant.user_service.dto.CategoryResponseDto;
import com.smartrestaurant.user_service.dto.DishesResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "menu-service")
public interface MenuServiceClient {
    @GetMapping("/api/dishes/filter")
    List<DishesResponseDto> filterDishes(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                         @RequestParam(value = "availability", required = false) Boolean availability);

    @GetMapping("/api/categories")
    List<CategoryResponseDto> getAllCategories();
}
