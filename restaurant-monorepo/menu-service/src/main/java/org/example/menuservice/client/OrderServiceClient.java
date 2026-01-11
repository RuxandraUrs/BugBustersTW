package org.example.menuservice.client;


import org.example.menuservice.dto.OrderResponseDto;
import org.example.menuservice.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", configuration = FeignClientInterceptor.class)
public interface OrderServiceClient {

    // toate comenziile-admin
    @GetMapping("/api/orders")
    List<OrderResponseDto> getAllOrders();

    //comenzi dupa numele clientului-client
    @GetMapping("/api/orders/search/client")
    List<OrderResponseDto> getOrdersByClient(@RequestParam("name") String clientName);
}