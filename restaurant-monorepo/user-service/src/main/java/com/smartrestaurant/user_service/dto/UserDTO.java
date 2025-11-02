package com.smartrestaurant.user_service.dto;

import com.smartrestaurant.user_service.entity.Role;

public class UserDTO {

    private String name;
    private String email;
    private String address;
    private String phone;
    private Role role_name;

    public UserDTO() {}
    public UserDTO(String name, String email, String address, String phone, Role role_name) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.role_name = role_name;
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

    public Role getRole_name() {
        return role_name;
    }

    public void setRole_name(Role role_name) {
        this.role_name = role_name;
    }

}
