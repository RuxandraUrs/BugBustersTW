package com.smartrestaurant.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order-service")
public class OrderServiceClient {

}
