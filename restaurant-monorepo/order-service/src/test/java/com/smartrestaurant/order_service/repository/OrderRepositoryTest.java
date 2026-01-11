package com.smartrestaurant.order_service.repository;

import com.smartrestaurant.order_service.entity.Order;
import com.smartrestaurant.order_service.entity.OrderDetail;
import com.smartrestaurant.order_service.entity.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order order1;
    private Order order2;
    private Order order3;

    @BeforeEach
    void setUp() {
        order1 = new Order();
        order1.setClientId(1L);
        order1.setDeliveryAddress("Address 1");
        order1.setStatus(OrderStatus.PLACED);
        order1.setTotalPrice(new BigDecimal("50.00"));
        order1.setOrderDetails(new ArrayList<>());

        OrderDetail detail1 = new OrderDetail();
        detail1.setDishId(1L);
        detail1.setQuantity(2);
        detail1.setPriceAtOrder(new BigDecimal("50.00"));
        detail1.setOrder(order1);
        order1.getOrderDetails().add(detail1);

        order2 = new Order();
        order2.setClientId(2L);
        order2.setDeliveryAddress("Address 2");
        order2.setStatus(OrderStatus.READY_FOR_PREPARATION);
        order2.setTotalPrice(new BigDecimal("120.00"));
        order2.setOrderDetails(new ArrayList<>());

        OrderDetail detail2 = new OrderDetail();
        detail2.setDishId(2L);
        detail2.setQuantity(3);
        detail2.setPriceAtOrder(new BigDecimal("120.00"));
        detail2.setOrder(order2);
        order2.getOrderDetails().add(detail2);


        order3 = new Order();
        order3.setClientId(1L);
        order3.setDeliveryAddress("Address 3");
        order3.setStatus(OrderStatus.PLACED);
        order3.setTotalPrice(new BigDecimal("30.00"));
        order3.setOrderDetails(new ArrayList<>());

        OrderDetail detail3 = new OrderDetail();
        detail3.setDishId(3L);
        detail3.setQuantity(1);
        detail3.setPriceAtOrder(new BigDecimal("30.00"));
        detail3.setOrder(order3);
        order3.getOrderDetails().add(detail3);


        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();
    }

    @Test
    void testFindByStatus_PlacedOrders() {
        // Act
        List<Order> placedOrders = orderRepository.findByStatus(OrderStatus.PLACED);

        // Assert
        assertThat(placedOrders).hasSize(2);
        assertThat(placedOrders)
                .extracting(Order::getStatus)
                .containsOnly(OrderStatus.PLACED);
        assertThat(placedOrders)
                .extracting(Order::getClientId)
                .contains(1L, 1L);
    }

    @Test
    void testFindByStatus_ReadyForPreparationOrders() {
        // Act
        List<Order> readyOrders = orderRepository.findByStatus(OrderStatus.READY_FOR_PREPARATION);

        // Assert
        assertThat(readyOrders).hasSize(1);
        assertThat(readyOrders.get(0).getStatus())
                .isEqualTo(OrderStatus.READY_FOR_PREPARATION);
        assertThat(readyOrders.get(0).getTotalPrice())
                .isEqualByComparingTo(new BigDecimal("120.00"));
    }

    @Test
    void testFindByStatus_NoOrders() {
        // Act
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);

        // Assert
        assertThat(deliveredOrders).isEmpty();
    }

    @Test
    void testFindByTotalPriceGreaterThan_OneLargeOrder() {
        // Act
        List<Order> largeOrders = orderRepository.findByTotalPriceGreaterThan(
                new BigDecimal("100.00")
        );

        // Assert
        assertThat(largeOrders).hasSize(1);
        assertThat(largeOrders.get(0).getTotalPrice())
                .isGreaterThan(new BigDecimal("100.00"));
        assertThat(largeOrders.get(0).getClientId()).isEqualTo(2L);
    }

    @Test
    void testFindByTotalPriceGreaterThan_MultipleOrders() {
        // Act
        List<Order> ordersAbove25 = orderRepository.findByTotalPriceGreaterThan(
                new BigDecimal("25.00")
        );

        // Assert
        assertThat(ordersAbove25).hasSize(3);
        assertThat(ordersAbove25)
                .extracting(Order::getTotalPrice)
                .allMatch(price -> price.compareTo(new BigDecimal("25.00")) > 0);
    }


    @Test
    void testSaveOrder_WithDetails() {
        // Arrange
        Order newOrder = new Order();
        newOrder.setClientId(3L);
        newOrder.setDeliveryAddress("New Address");
        newOrder.setStatus(OrderStatus.PLACED);
        newOrder.setTotalPrice(new BigDecimal("75.00"));
        newOrder.setOrderDetails(new ArrayList<>());

        OrderDetail newDetail = new OrderDetail();
        newDetail.setDishId(4L);
        newDetail.setQuantity(5);
        newDetail.setPriceAtOrder(new BigDecimal("75.00"));
        newDetail.setOrder(newOrder);
        newOrder.getOrderDetails().add(newDetail);

        // Act
        Order savedOrder = orderRepository.save(newOrder);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElse(null);
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getClientId()).isEqualTo(3L);
        assertThat(foundOrder.getOrderDetails()).hasSize(1);
        assertThat(foundOrder.getOrderDetails().get(0).getDishId()).isEqualTo(4L);
        assertThat(foundOrder.getPlacementDate()).isNotNull();
    }

    @Test
    void testUpdateOrder_Status() {
        // Arrange
        Order order = orderRepository.findById(order1.getId()).orElseThrow();

        // Act
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Order updatedOrder = orderRepository.findById(order1.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    void testDeleteOrder_CascadeDetails() {
        // Arrange
        Long orderId = order1.getId();

        // Act
        orderRepository.deleteById(orderId);
        entityManager.flush();

        // Assert
        assertThat(orderRepository.findById(orderId)).isEmpty();
        // OrderDetails should also be deleted due to cascade
    }

    @Test
    void testFindAll_ReturnsAllOrders() {
        // Act
        List<Order> allOrders = orderRepository.findAll();

        // Assert
        assertThat(allOrders).hasSize(3);
    }
}