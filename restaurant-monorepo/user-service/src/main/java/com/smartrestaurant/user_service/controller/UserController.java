package com.smartrestaurant.user_service.controller;


import com.smartrestaurant.user_service.dto.CreateUserDTO;
import com.smartrestaurant.user_service.dto.UserDTO;
import com.smartrestaurant.user_service.service.IUserService;
import com.smartrestaurant.user_service.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        System.out.println("Received DTO: " + createUserDTO);
        UserDTO createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

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

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteUser(@Valid @PathVariable String email){
        boolean deleted=userService.deleteUser(email);
        return ResponseEntity.ok(deleted);
    }

    //Extra endpoints
    @GetMapping("/clients")
    public ResponseEntity<List<UserDTO>> getClients() {
        List<UserDTO> clients = userService.getClients();
        return ResponseEntity.status(HttpStatus.OK).body(clients);
    }

    @GetMapping("/employees/count")
    public ResponseEntity<Long> countEmployees() {
        Long employeeCount = userService.countEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(employeeCount);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> findByName(@RequestParam String name) {
        List<UserDTO> users = userService.findByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
}
