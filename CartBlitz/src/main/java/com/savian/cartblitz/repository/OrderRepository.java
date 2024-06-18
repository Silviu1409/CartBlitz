package com.savian.cartblitz.repository;

import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerCustomerId(Long customerId);
    List<Order> findByStatus(OrderStatusEnum status);
    List<Order> findByCustomerCustomerIdAndStatus(Long customerId, OrderStatusEnum status);
}
