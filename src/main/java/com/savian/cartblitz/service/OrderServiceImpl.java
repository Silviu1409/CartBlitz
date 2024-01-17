package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.OrderInProgressException;
import com.savian.cartblitz.exception.OrderNotFoundException;
import com.savian.cartblitz.mapper.OrderMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public Optional<OrderDto> getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isPresent()){
            return order.map(orderMapper::orderToOrderDto);
        }
        else {
            throw new OrderNotFoundException(orderId);
        }
    }

    @Override
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        return orderRepository.findByCustomerCustomerId(customerId).stream().map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public List<OrderDto> gelOrdersByStatus(OrderStatusEnum status) {
        return orderRepository.findByStatus(status).stream().map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public OrderDto completeOrder(Long orderId) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isPresent()){
            Order prevOrder = optOrder.get();

            prevOrder.setStatus(OrderStatusEnum.COMPLETED);
            prevOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));

            Order savedOrder = orderRepository.save(prevOrder);
            return orderMapper.orderToOrderDto(savedOrder);
        }
        else{
            throw new OrderNotFoundException(orderId);
        }
    }

    @Override
    public OrderDto modifyTotalAmount(Long orderId, BigDecimal amount) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isPresent()){
            Order prevOrder = optOrder.get();

            prevOrder.setTotalAmount((prevOrder.getTotalAmount().add(amount)).max(BigDecimal.valueOf(0)));
            prevOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));

            Order savedOrder = orderRepository.save(prevOrder);
            return orderMapper.orderToOrderDto(savedOrder);
        }
        else{
            throw new OrderNotFoundException(orderId);
        }
    }

    @Override
    public OrderDto saveOrder(Long customerId) {
        if (orderRepository.findByCustomerCustomerIdAndStatus(customerId, OrderStatusEnum.CART).isEmpty()) {
            Order order = new Order();

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException(customerId));

            order.setCustomer(customer);
            order.setTotalAmount(BigDecimal.valueOf(0));
            order.setStatus(OrderStatusEnum.CART);
            order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));

            Order savedOrder = orderRepository.save(order);
            return orderMapper.orderToOrderDto(savedOrder);
        }
        else {
            throw new OrderInProgressException();
        }
    }

    @Override
    public OrderDto updateOrder(Long orderId, Long customerId) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isPresent()){
            Order prevOrder = optOrder.get();

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException(customerId));

            prevOrder.setCustomer(customer);
            prevOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));

            Order savedOrder = orderRepository.save(prevOrder);
            return orderMapper.orderToOrderDto(savedOrder);
        }
        else{
            throw new OrderNotFoundException(orderId);
        }
    }

    @Override
    public void removeOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()){
            orderRepository.deleteById(orderId);
        }
        else{
            throw new OrderNotFoundException(orderId);
        }
    }
}
