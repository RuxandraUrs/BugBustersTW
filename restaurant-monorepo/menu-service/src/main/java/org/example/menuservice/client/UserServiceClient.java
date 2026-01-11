package org.example.menuservice.client;

import org.example.menuservice.config.FeignClientInterceptor;
import org.example.menuservice.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", configuration = FeignClientInterceptor.class)
public interface UserServiceClient {

    @GetMapping("/api/users/clients")
    List<UserResponseDto> getClients();

    @GetMapping("/api/users/employees/count")
    Long countEmployees();

    @GetMapping("/api/users/search")
    List<UserResponseDto> searchUsers(@RequestParam("name") String name);
}