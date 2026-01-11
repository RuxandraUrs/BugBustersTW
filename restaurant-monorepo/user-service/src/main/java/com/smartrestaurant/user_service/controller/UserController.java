package com.smartrestaurant.user_service.controller;

import com.smartrestaurant.user_service.client.MenuServiceClient;
import com.smartrestaurant.user_service.client.OrderServiceClient;
import com.smartrestaurant.user_service.dto.*;
import com.smartrestaurant.user_service.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path="/api/users")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private MenuServiceClient menuServiceClient;
    @Autowired
    private OrderServiceClient orderServiceClient;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        System.out.println("Received DTO: " + createUserDTO);
        UserDTO createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try{
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error ocurred: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteUser(@Valid @PathVariable String email){
        boolean deleted=userService.deleteUser(email);
        return ResponseEntity.ok(deleted);
    }

    //Extra endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/clients")
    public ResponseEntity<List<UserDTO>> getClients() {
        List<UserDTO> clients = userService.getClients();
        return ResponseEntity.status(HttpStatus.OK).body(clients);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employees/count")
    public ResponseEntity<Long> countEmployees() {
        Long employeeCount = userService.countEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(employeeCount);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> findByName(@RequestParam String name) {
        List<UserDTO> users = userService.findByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //http://localhost:8080/api/users/dashboard/categories
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard/categories")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(menuServiceClient.getAllCategories());
    }

    //http://localhost:8080/api/users/dashboard/dishes/filter?categoryId=2
    //http://localhost:8080/api/users/dashboard/dishes/filter?availability=true
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard/dishes/filter")
    public ResponseEntity<List<DishesResponseDto>> getFilteredDishes(@RequestParam(required = false) Integer categoryId, @RequestParam(required = false) Boolean availability) {
        return ResponseEntity.ok(menuServiceClient.filterDishes(categoryId, availability));
    }

    //http://localhost:8080/api/users/dashboard/orders
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderServiceClient.getAllOrders());
    }

    //http://localhost:8080/api/users/dashboard/large_orders
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard/large_orders")
    public ResponseEntity<List<OrderResponseDto>> getLargeOrders(@RequestParam(defaultValue = "100.00")BigDecimal minTotal) {
        return ResponseEntity.ok(orderServiceClient.getLargeOrders(minTotal));
    }
}
