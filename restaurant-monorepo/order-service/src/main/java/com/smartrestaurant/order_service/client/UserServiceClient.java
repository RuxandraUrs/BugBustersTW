package com.smartrestaurant.order_service.client;

import com.smartrestaurant.order_service.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/search/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/users/search")
    List<UserResponseDto> searchUsersByName(@RequestParam("name") String name);
}