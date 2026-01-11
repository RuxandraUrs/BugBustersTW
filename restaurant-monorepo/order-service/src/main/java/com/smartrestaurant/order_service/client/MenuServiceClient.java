package com.smartrestaurant.order_service.client;
import com.smartrestaurant.order_service.dto.DishResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "menu-service")
public interface MenuServiceClient {

    @GetMapping("/api/dishes/{id}")
    DishResponseDto getDishById(@PathVariable("id") Long id);

    @GetMapping("/api/dishes/search")
    List<DishResponseDto> searchDishesByName(@RequestParam("name") String name);
}
