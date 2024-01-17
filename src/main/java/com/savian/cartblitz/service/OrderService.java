package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDto> getAllOrders();
    Optional<OrderDto> getOrderById(Long orderId);

    List<OrderDto> getOrdersByCustomerId(Long customerId);
    List<OrderDto> gelOrdersByStatus(OrderStatusEnum status);

    OrderDto completeOrder(Long orderId);
    OrderDto modifyTotalAmount(Long orderId, BigDecimal amount);

    OrderDto saveOrder(Long customerId);
    OrderDto updateOrder(Long orderId, Long customerId);
    void removeOrderById(Long orderId);
}
