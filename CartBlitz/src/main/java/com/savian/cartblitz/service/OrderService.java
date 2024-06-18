package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.model.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDto> getAllOrders();
    Optional<OrderDto> getOrderById(Long orderId);

    List<OrderDto> getOrdersByCustomerId(Long customerId);
    List<OrderDto> getOrdersByStatus(OrderStatusEnum status);
    List<OrderDto> getOrdersByCustomerIdAndStatus(Long customerId, OrderStatusEnum status);

    OrderDto completeOrder(Long orderId);
    OrderDto modifyTotalAmount(Long orderId, BigDecimal amount);

    OrderDto saveOrder(Long customerId);
    void saveOrUpdateOrder(OrderDto orderDto);
    OrderDto updateOrder(Long orderId, Long customerId);
    void updateTotalAmount(Long orderId, BigDecimal amount);
    void removeOrderById(Long orderId);
}
