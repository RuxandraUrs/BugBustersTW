package com.smartrestaurant.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "orderdetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name ="dish_id", nullable = false)
    private Long dishId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name="price_at_order", nullable = false)
    private BigDecimal priceAtOrder;



}
