package com.smartrestaurant.order_service.service;

import com.smartrestaurant.order_service.client.MenuServiceClient;
import com.smartrestaurant.order_service.client.UserServiceClient;
import com.smartrestaurant.order_service.dto.*;
import com.smartrestaurant.order_service.entity.Order;
import com.smartrestaurant.order_service.entity.OrderDetail;
import com.smartrestaurant.order_service.entity.OrderStatus;
import com.smartrestaurant.order_service.mapper.OrderMapper;
import com.smartrestaurant.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service implementation for managing restaurant orders.
 * Handles order creation, retrieval, updates, and search operations.
 * Communicates with Menu Service and User Service for data aggregation.
 *
 * @author [Vilcu Andreea]
 * @version 1.0
 */

@Service
@Transactional
public  class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;
    private final UserServiceClient userServiceClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            MenuServiceClient menuServiceClient,
                            UserServiceClient userServiceClient) {
        this.orderRepository = orderRepository;
        this.menuServiceClient = menuServiceClient;
        this.userServiceClient = userServiceClient;
    }


    /**
     * Creates a new order in the system.
     * Validates that the client exists in the user service and retrieves dish information
     * from the menu service before creating the order.
     *
     * @param request The order request containing client ID, delivery address, and items
     * @return OrderResponseDto The created order with generated ID and calculated total price
     * @throws NoSuchElementException if the client ID does not exist in the user service
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {

        try {
            userServiceClient.getUserById(request.getClientId());
        } catch (Exception e) {
            throw new NoSuchElementException("Client with  ID " + request.getClientId()  +" does not exist!");
        }

        Order order = OrderMapper.toEntity(request);

        List<OrderDetail> details = mapDetailsRequestToEntity(request.getItems(), order);

        order.setOrderDetails(details);
        BigDecimal total = details.stream()
                .map(OrderDetail::getPriceAtOrder)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(total);
        order.setStatus(OrderStatus.PLACED);

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toResponseDto(savedOrder);

    }

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId The ID of the order to retrieve
     * @return OrderResponseDto The order details
     * @throws NoSuchElementException if no order exists with the specified ID
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public OrderResponseDto getOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .map(OrderMapper::toResponseDto)
                .orElseThrow(() -> new NoSuchElementException("Comanda nu a fost gasita cu ID: " + orderId));
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return List of OrderResponseDto containing all orders in the system
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing order's delivery address and items.
     * Recalculates the total price based on the new items.
     * The order status is not modified by this operation.
     *
     * @param orderId The ID of the order to update
     * @param updateRequest The new order data containing delivery address and items
     * @return OrderResponseDto The updated order
     * @throws NoSuchElementException if no order exists with the specified ID
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto updateRequest) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found" + orderId));

        existingOrder.setDeliveryAddress(updateRequest.getDeliveryAddress());

        existingOrder.getOrderDetails().clear();
        List<OrderDetail> newDetails = mapDetailsRequestToEntity(updateRequest.getItems(), existingOrder);
        existingOrder.getOrderDetails().addAll(newDetails);

        existingOrder.setTotalPrice(calculateTotalPrice(updateRequest.getItems()));
        return OrderMapper.toResponseDto(orderRepository.save(existingOrder));

    }

    /**
     * Updates the status of an order to READY_FOR_PREPARATION.
     * The order must be in PLACED status to be updated.
     *
     * @param id The ID of the order to update
     * @return OrderResponseDto The updated order with new status
     * @throws NoSuchElementException if the order is not found
     * @throws IllegalStateException if the order is not in PLACED status
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public OrderResponseDto updateStatusReadyForPreparation(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + id));

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException(
                    "Cannot change status to READY_FOR_PREPARATION. Current status is: " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.READY_FOR_PREPARATION);
        Order updatedOrder = orderRepository.save(order);

        return OrderMapper.toResponseDto(updatedOrder);
    }

    /**
     * Deletes an order from the database.
     * Returns the deleted order data before removal.
     *
     * @param id The ID of the order to delete
     * @return OrderResponseDto The deleted order data
     * @throws NoSuchElementException if no order exists with the specified ID
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public OrderResponseDto deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found" + id));

        OrderResponseDto responseDto = OrderMapper.toResponseDto(order);

        orderRepository.delete(order);

        return responseDto;
    }

    /**
     * Retrieves detailed information about an order including client email and dish names.
     * This method aggregates data from order-service, user-service, and menu-service.
     *
     * @param id The ID of the order to retrieve
     * @return OrderDetailedResponseDto containing order details with client email and dish names
     * @throws NoSuchElementException if the order is not found
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public OrderDetailedResponseDto getOrderDetails(Long id) {
        Order order  = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found " + id));

        UserResponseDto userDto = userServiceClient.getUserById(order.getClientId());

        List<String> dishNames = order.getOrderDetails().stream()
                .map(detail -> {
                    return menuServiceClient.getDishById(detail.getDishId()).getName();
                })
                .collect(Collectors.toList());

        OrderDetailedResponseDto response = new OrderDetailedResponseDto();
        response.setOrderId(order.getId());
        response.setTotal(order.getTotalPrice());
        response.setAddress(order.getDeliveryAddress());
        response.setClientEmail(userDto.getEmail());
        response.setDishNames(dishNames);

        return response;
    }

    /**
     * Searches for orders placed by clients whose name contains the specified string.
     * Queries the user service to find matching clients, then filters orders by those client IDs.
     *
     * @param name The name or partial name to search for
     * @return List of OrderResponseDto for orders placed by matching clients, empty list if no matches
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public List<OrderResponseDto> getOrdersByClientName(String name) {
        List<UserResponseDto> users = userServiceClient.searchUsersByName(name);

        if(users.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = users.stream()
                .map(UserResponseDto::getId)
                .toList();

        return orderRepository.findAll().stream()
                .filter(order -> userIds.contains(order.getClientId()))
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());

    }


    /**
     * Searches for orders containing dishes whose name matches the specified string.
     * Queries the menu service to find matching dishes, then filters orders containing those dishes.
     *
     * @param dishName The dish name or partial name to search for
     * @return List of OrderResponseDto for orders containing matching dishes, empty list if no matches
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public List<OrderResponseDto> getOrdersByDishName(String dishName) {
        List<DishResponseDto> dishes = menuServiceClient.searchDishesByName(dishName);

        if (dishes.isEmpty()) {
            return List.of();
        }

        List<Long> dishIds = dishes.stream()
                .map(DishResponseDto::getId)
                .map(Long::valueOf)
                .toList();

        return orderRepository.findAll().stream()
                .filter(order -> order.getOrderDetails().stream()
                        .anyMatch(detail -> dishIds.contains(detail.getDishId())))
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves all orders with a specific status.
     * Used for filtering orders by their processing stage (PLACED, READY_FOR_PREPARATION, etc.).
     *
     * @param status The order status to filter by
     * @return List of OrderResponseDto for orders with the specified status
     * @throws IllegalArgumentException if the status parameter is null
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return orderRepository.findByStatus(status).stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves orders with a total price greater than the specified minimum amount,
     * sorted in descending order by total price.
     * Useful for identifying high-value orders or generating reports on large transactions.
     *
     * @param minTotal The minimum total price threshold (must be non-negative)
     * @return List of OrderResponseDto for orders exceeding the minimum total,
     *         sorted from highest to lowest total price
     * @throws IllegalArgumentException if minTotal is null or negative
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    @Override
    public List<OrderResponseDto> getLargeOrdersSortedByTotal(BigDecimal minTotal) {
        if(minTotal == null){
            throw new IllegalArgumentException("Minimum total cannot be null");
        }

        if (minTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum total cannot be negative");
        }

        List<Order> largeOrders = orderRepository.findByTotalPriceGreaterThan(minTotal);

        return largeOrders.stream()
                .sorted((o1, o2) -> o2.getTotalPrice().compareTo(o1.getTotalPrice()))
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    /**
     * Calculates the total price for a list of order items.
     * Fetches the current prices from Menu Service for each dish and multiplies by quantity.
     * This method is used during order updates to recalculate the total based on current menu prices.
     *
     * @param items The list of order items
     * @return BigDecimal The calculated total price based on current menu prices
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    private BigDecimal calculateTotalPrice(List<OrderDetailRequestDto> items) {
        return items.stream()
                .map(item -> {
                    DishResponseDto dish = menuServiceClient.getDishById(item.getDishId());
                    return dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Maps order detail request DTOs to OrderDetail entities.
     * Fetches current dish prices from Menu Service and calculates the price at order time.
     * This ensures orders maintain historical pricing even if menu prices change later.
     *
     * @param items The list of order item requests
     * @param order The parent order entity
     * @return List of OrderDetail entities with current prices and quantities
     * @author [Vilcu Andreea] - [11.01.2026]
     */
    private List<OrderDetail> mapDetailsRequestToEntity(List<OrderDetailRequestDto> items, Order order) {

        return items.stream()
                .map(itemReq -> {
                    DishResponseDto dish = menuServiceClient.getDishById(itemReq.getDishId());

                    OrderDetail detail = new OrderDetail();
                    detail.setDishId(Long.valueOf(dish.getId()));
                    detail.setQuantity(itemReq.getQuantity());
                    detail.setOrder(order);

                    BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                    detail.setPriceAtOrder(itemTotal);

                    return detail;
                })
                .collect(Collectors.toList());
    }
}
