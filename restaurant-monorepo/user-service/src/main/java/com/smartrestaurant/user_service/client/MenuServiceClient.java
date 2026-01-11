package com.smartrestaurant.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "menu-service")
public class MenuServiceClient {

}
