package com.smartrestaurant.order_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"Order\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "employee_id")
    private Long employeeId;

    @CreationTimestamp
    @Column(name = "placement_date", nullable = false, updatable = false)
    private ZonedDateTime placementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status = OrderStatus.PLACED;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "delivery_address", nullable = false, length = 200)
    private String deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();


    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails.clear();
        if (orderDetails != null) {
            this.orderDetails.addAll(orderDetails);
            orderDetails.forEach(detail -> detail.setOrder(this));
        }
    }


}
