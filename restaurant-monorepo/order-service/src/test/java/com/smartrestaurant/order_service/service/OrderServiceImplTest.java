package com.smartrestaurant.order_service.service;

import com.smartrestaurant.order_service.client.MenuServiceClient;
import com.smartrestaurant.order_service.client.UserServiceClient;
import com.smartrestaurant.order_service.dto.*;
import com.smartrestaurant.order_service.entity.Order;
import com.smartrestaurant.order_service.entity.OrderDetail;
import com.smartrestaurant.order_service.entity.OrderStatus;
import com.smartrestaurant.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuServiceClient menuServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDto orderRequestDto;
    private Order order;
    private DishResponseDto dishResponseDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        // Setup Order Request DTO
        OrderDetailRequestDto detailRequest = new OrderDetailRequestDto();
        detailRequest.setDishId(1L);
        detailRequest.setQuantity(2);

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setClientId(1L);
        orderRequestDto.setDeliveryAddress("Str. Test Nr. 1");
        orderRequestDto.setItems(Arrays.asList(detailRequest));

        order = new Order();
        order.setId(1L);
        order.setClientId(1L);
        order.setDeliveryAddress("Str. Test Nr. 1");
        order.setStatus(OrderStatus.PLACED);
        order.setTotalPrice(new BigDecimal("20.00"));
        order.setPlacementDate(ZonedDateTime.now());
        order.setOrderDetails(new ArrayList<>());

        OrderDetail detail = new OrderDetail();
        detail.setId(1L);
        detail.setDishId(1L);
        detail.setQuantity(2);
        detail.setPriceAtOrder(new BigDecimal("20.00"));
        detail.setOrder(order);
        order.getOrderDetails().add(detail);

        // Setup Dish Response
        dishResponseDto = new DishResponseDto();
        dishResponseDto.setId(1);
        dishResponseDto.setName("Pizza");
        dishResponseDto.setPrice(new BigDecimal("10.00"));

        // Setup User Response
        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setEmail("test@example.com");
        userResponseDto.setName("Test User");
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(userServiceClient.getUserById(anyLong())).thenReturn(userResponseDto);
        when(menuServiceClient.getDishById(anyLong())).thenReturn(dishResponseDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDto result = orderService.createOrder(orderRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.PLACED, result.getStatus());
        assertEquals(new BigDecimal("20.00"), result.getTotalPrice());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(userServiceClient, times(1)).getUserById(1L);
        verify(menuServiceClient, times(1)).getDishById(1L);
    }

    @Test
    void testCreateOrder_UserNotFound() {
        // Arrange
        when(userServiceClient.getUserById(anyLong()))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                orderService.createOrder(orderRequestDto)
        );
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderResponseDto result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.PLACED, result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                orderService.getOrderById(999L)
        );
    }

    @Test
    void testGetAllOrders_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<OrderResponseDto> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(menuServiceClient.getDishById(anyLong())).thenReturn(dishResponseDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDto result = orderService.updateOrder(1L, orderRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Str. Test Nr. 1", result.getDeliveryAddress());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrder_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                orderService.updateOrder(999L, orderRequestDto)
        );
    }

    @Test
    void testUpdateStatusReadyForPreparation_Success() {
        // Arrange
        order.setStatus(OrderStatus.PLACED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setStatus(OrderStatus.READY_FOR_PREPARATION);
            return savedOrder;
        });

        // Act
        OrderResponseDto result = orderService.updateStatusReadyForPreparation(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.READY_FOR_PREPARATION, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateStatusReadyForPreparation_InvalidStatus() {
        // Arrange
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                orderService.updateStatusReadyForPreparation(1L)
        );
    }

    @Test
    void testDeleteOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(any(Order.class));

        // Act
        OrderResponseDto result = orderService.deleteOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void testDeleteOrder_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                orderService.deleteOrder(999L)
        );
    }

    @Test
    void testGetOrderDetails_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userServiceClient.getUserById(1L)).thenReturn(userResponseDto);
        when(menuServiceClient.getDishById(1L)).thenReturn(dishResponseDto);

        // Act
        OrderDetailedResponseDto result = orderService.getOrderDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("test@example.com", result.getClientEmail());
        assertEquals(1, result.getDishNames().size());
        assertEquals("Pizza", result.getDishNames().get(0));
    }

    @Test
    void testGetOrdersByClientName_Success() {
        // Arrange
        List<UserResponseDto> users = Arrays.asList(userResponseDto);
        when(userServiceClient.searchUsersByName("Test")).thenReturn(users);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        // Act
        List<OrderResponseDto> result = orderService.getOrdersByClientName("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userServiceClient, times(1)).searchUsersByName("Test");
    }

    @Test
    void testGetOrdersByClientName_NoUsers() {
        // Arrange
        when(userServiceClient.searchUsersByName("NonExistent")).thenReturn(List.of());

        // Act
        List<OrderResponseDto> result = orderService.getOrdersByClientName("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOrdersByDishName_Success() {
        // Arrange
        List<DishResponseDto> dishes = Arrays.asList(dishResponseDto);
        when(menuServiceClient.searchDishesByName("Pizza")).thenReturn(dishes);
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));

        // Act
        List<OrderResponseDto> result = orderService.getOrdersByDishName("Pizza");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatus_Success() {
        // Arrange
        when(orderRepository.findByStatus(OrderStatus.PLACED))
                .thenReturn(Arrays.asList(order));

        // Act
        List<OrderResponseDto> result = orderService.getOrdersByStatus(OrderStatus.PLACED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PLACED, result.get(0).getStatus());
    }

    @Test
    void testGetOrdersByStatus_NullStatus() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                orderService.getOrdersByStatus(null)
        );
    }

    @Test
    void testGetLargeOrdersSortedByTotal_Success() {
        // Arrange
        BigDecimal minTotal = new BigDecimal("15.00");
        when(orderRepository.findByTotalPriceGreaterThan(minTotal))
                .thenReturn(Arrays.asList(order));

        // Act
        List<OrderResponseDto> result = orderService.getLargeOrdersSortedByTotal(minTotal);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getTotalPrice().compareTo(minTotal) > 0);
    }

    @Test
    void testGetLargeOrdersSortedByTotal_NullMinTotal() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                orderService.getLargeOrdersSortedByTotal(null)
        );
    }

    @Test
    void testGetLargeOrdersSortedByTotal_NegativeMinTotal() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                orderService.getLargeOrdersSortedByTotal(new BigDecimal("-10.00"))
        );
    }

}