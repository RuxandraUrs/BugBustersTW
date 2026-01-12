package com.smartrestaurant.user_service.entity;

import com.smartrestaurant.user_service.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name="\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", unique = true)
    private String name;

    @Column(name="email", unique = true)
    private String email;

    @Column(name="password_hash", nullable=false)
    private String password_hash;

    @Column(name="address", nullable=false)
    private String address;

    @Column(name="phone")
    private String phone;

    @Column(name="salary")
    private Float salary;

    @Enumerated(EnumType.ORDINAL)
    @JoinColumn(name="role")
    private Role role;

    public User(String name, String email, String address, String phone, Role roleName, Float salary) {}

    public User(Long id, String name, String email, String address, String phone, Role roleName, Float salary) {}

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
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

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
