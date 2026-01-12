package com.smartrestaurant.user_service.dto;

import com.smartrestaurant.user_service.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserDTO {
    @NotBlank(message="Name is required")
    private String name;

    @Email(message="Email is not valid")
    private String email;

    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d).{6,}",
    message = "Password must have at least 6 characters and contain at least one uppercase letter and a special symbol\n")
    private String password;

    @NotBlank(message="Address is required")
    private String address;

    @Size(min=10, max=15, message="Phone must have between 10 and 15 digits\n")
    private String phone;

    private Role roleName;

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    private Float salary;
    public CreateUserDTO(){}

    public CreateUserDTO(String name, String email, String password, String address, String phone, Role roleName, Float salary) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.roleName = roleName;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRoleName() {
        return roleName;
    }

    public void setRoleName(Role roleName) {
        this.roleName = roleName;
    }
}
