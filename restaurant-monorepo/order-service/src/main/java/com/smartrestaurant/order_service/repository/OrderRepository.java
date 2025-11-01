package com.smartrestaurant.order_service.repository;

import com.smartrestaurant.order_service.entity.Order;
import com.smartrestaurant.order_service.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByTotalPriceGreaterThan(BigDecimal price);

}
