package com.smartrestaurant.user_service.client;

import com.smartrestaurant.user_service.dto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/api/orders")
    List<OrderResponseDto> getAllOrders();

    @GetMapping("/api/orders/large_orders")
    List<OrderResponseDto> getLargeOrders(@RequestParam("minTotal") BigDecimal minTotal);
}
