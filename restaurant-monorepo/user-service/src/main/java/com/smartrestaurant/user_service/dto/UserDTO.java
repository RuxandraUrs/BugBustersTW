package com.smartrestaurant.user_service.dto;

import com.smartrestaurant.user_service.enums.Role;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private Role roleName;
    private Float salary;

    public UserDTO() {}

    public UserDTO(Long id, String name, String email, String address, String phone, Role role, Float salary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.roleName = role;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id=id;
    }
    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public void setRoleName(Role roleName) {
        this.roleName = roleName;
    }

    public UserDTO(String name, String email, String address, String phone, Role roleName, Float salary) {
        this.name = name;
        this.email = email;
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

    public void setRole_name(Role role_name) {
        this.roleName = roleName;
    }

}
