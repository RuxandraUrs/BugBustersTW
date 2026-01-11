package com.smartrestaurant.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartrestaurant.order_service.dto.*;
import com.smartrestaurant.order_service.entity.OrderStatus;
import com.smartrestaurant.order_service.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderServiceImpl orderService;

    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;
    private OrderDetailResponseDto orderDetailResponseDto;

    @BeforeEach
    void setUp() {
        // Setup Order Request
        OrderDetailRequestDto detailRequest = new OrderDetailRequestDto();
        detailRequest.setDishId(1L);
        detailRequest.setQuantity(2);

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setClientId(1L);
        orderRequestDto.setDeliveryAddress("Str. Test Nr. 1");
        orderRequestDto.setItems(Arrays.asList(detailRequest));

        // Setup Order Detail Response
        orderDetailResponseDto = new OrderDetailResponseDto();
        orderDetailResponseDto.setDishId(1L);
        orderDetailResponseDto.setQuantity(2);
        orderDetailResponseDto.setPriceAtOrder(new BigDecimal("20.00"));

        // Setup Order Response
        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);
        orderResponseDto.setClientId(1L);
        orderResponseDto.setStatus(OrderStatus.PLACED);
        orderResponseDto.setTotalPrice(new BigDecimal("20.00"));
        orderResponseDto.setDeliveryAddress("Str. Test Nr. 1");
        orderResponseDto.setItems(Arrays.asList(orderDetailResponseDto));
        orderResponseDto.setPlacementDate(ZonedDateTime.now());
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderRequestDto.class)))
                .thenReturn(orderResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.totalPrice").value(20.00));

        verify(orderService, times(1)).createOrder(any(OrderRequestDto.class));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponseDto);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deliveryAddress").value("Str. Test Nr. 1"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999L))
                .thenThrow(new NoSuchElementException("Order not found"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOrders_Success() throws Exception {
        List<OrderResponseDto> orders = Arrays.asList(orderResponseDto);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PLACED"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testUpdateOrder_Success() throws Exception {
        when(orderService.updateOrder(eq(1L), any(OrderRequestDto.class)))
                .thenReturn(orderResponseDto);

        mockMvc.perform(put("/api/orders/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).updateOrder(eq(1L), any(OrderRequestDto.class));
    }

    @Test
    void testUpdateOrder_NotFound() throws Exception {
        when(orderService.updateOrder(eq(999L), any(OrderRequestDto.class)))
                .thenThrow(new NoSuchElementException("Order not found"));

        mockMvc.perform(put("/api/orders/999/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMarkOrderReady_Success() throws Exception {
        orderResponseDto.setStatus(OrderStatus.READY_FOR_PREPARATION);
        when(orderService.updateStatusReadyForPreparation(1L))
                .thenReturn(orderResponseDto);

        mockMvc.perform(patch("/api/orders/1/status_ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY_FOR_PREPARATION"));

        verify(orderService, times(1)).updateStatusReadyForPreparation(1L);
    }

    @Test
    void testMarkOrderReady_InvalidStatus() throws Exception {
        when(orderService.updateStatusReadyForPreparation(1L))
                .thenThrow(new IllegalStateException("Invalid status transition"));

        mockMvc.perform(patch("/api/orders/1/status_ready"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOrder_Success() throws Exception {
        when(orderService.deleteOrder(1L)).thenReturn(orderResponseDto);

        mockMvc.perform(delete("/api/orders/1/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    void testGetAllOrdersByStatus_Success() throws Exception {
        List<OrderResponseDto> orders = Arrays.asList(orderResponseDto);
        when(orderService.getOrdersByStatus(OrderStatus.PLACED))
                .thenReturn(orders);

        mockMvc.perform(get("/api/orders/status/PLACED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PLACED"));

        verify(orderService, times(1)).getOrdersByStatus(OrderStatus.PLACED);
    }

    @Test
    void testGetAllOrdersByStatus_InvalidStatus() throws Exception {
        mockMvc.perform(get("/api/orders/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetLargeOrders_Success() throws Exception {
        List<OrderResponseDto> orders = Arrays.asList(orderResponseDto);
        when(orderService.getLargeOrdersSortedByTotal(any(BigDecimal.class)))
                .thenReturn(orders);

        mockMvc.perform(get("/api/orders/large_orders")
                        .param("minTotal", "50.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalPrice").value(20.00));

        verify(orderService, times(1))
                .getLargeOrdersSortedByTotal(any(BigDecimal.class));
    }

    @Test
    void testGetOrderDetails_Success() throws Exception {
        OrderDetailedResponseDto detailedResponse = new OrderDetailedResponseDto();
        detailedResponse.setOrderId(1L);
        detailedResponse.setClientEmail("test@example.com");
        detailedResponse.setTotal(new BigDecimal("20.00"));
        detailedResponse.setAddress("Str. Test Nr. 1");
        detailedResponse.setDishNames(Arrays.asList("Pizza"));

        when(orderService.getOrderDetails(1L)).thenReturn(detailedResponse);

        mockMvc.perform(get("/api/orders/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.clientEmail").value("test@example.com"))
                .andExpect(jsonPath("$.dishNames[0]").value("Pizza"));

        verify(orderService, times(1)).getOrderDetails(1L);
    }

    @Test
    void testSearchOrdersByClient_Success() throws Exception {
        List<OrderResponseDto> orders = Arrays.asList(orderResponseDto);
        when(orderService.getOrdersByClientName("Test User")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/search/client")
                        .param("name", "Test User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientId").value(1));

        verify(orderService, times(1)).getOrdersByClientName("Test User");
    }

    @Test
    void testSearchOrdersByDish_Success() throws Exception {
        List<OrderResponseDto> orders = Arrays.asList(orderResponseDto);
        when(orderService.getOrdersByDishName("Pizza")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/search/dish")
                        .param("name", "Pizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(orderService, times(1)).getOrdersByDishName("Pizza");
    }


}