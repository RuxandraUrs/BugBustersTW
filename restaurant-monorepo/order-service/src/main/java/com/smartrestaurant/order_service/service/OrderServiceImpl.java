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

    @Override
    public OrderResponseDto getOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .map(OrderMapper::toResponseDto)
                .orElseThrow(() -> new NoSuchElementException("Comanda nu a fost gasita cu ID: " + orderId));
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

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

    @Override
    public OrderResponseDto deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found" + id));

        OrderResponseDto responseDto = OrderMapper.toResponseDto(order);

        orderRepository.delete(order);

        return responseDto;
    }

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


    @Override
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return orderRepository.findByStatus(status).stream()
                .map(OrderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

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


    private BigDecimal calculateTotalPrice(List<OrderDetailRequestDto> items) {
        return items.stream()
                .map(item -> BigDecimal.valueOf(10.00).multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    private List<OrderDetail> mapDetailsRequestToEntity(List<OrderDetailRequestDto> items, Order order) {

        return items.stream()
                .map(itemReq -> {
                    // 2. Apelam Menu Service pentru a gasi produsul real
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
