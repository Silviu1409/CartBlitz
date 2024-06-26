package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.OrderInProgressException;
import com.savian.cartblitz.exception.OrderNotFoundException;
import com.savian.cartblitz.mapper.OrderMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.OrderProductRepository;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderMapper orderMapper;
    private CouponServiceProxy couponServiceProxy;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository, OrderProductRepository orderProductRepository, OrderMapper orderMapper, CouponServiceProxy couponServiceProxy) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderMapper = orderMapper;
        this.couponServiceProxy = couponServiceProxy;
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
    @Transactional
    public List<OrderDto> getOrdersByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            List<Order> orders = customer.getOrders();
            return orders.stream().map(orderMapper::orderToOrderDto).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<OrderDto> getOrdersByStatus(OrderStatusEnum status) {
        return orderRepository.findByStatus(status).stream().map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public List<OrderDto> getOrdersByCustomerIdAndStatus(Long customerId, OrderStatusEnum status) {
        return orderRepository.findByCustomerCustomerIdAndStatus(customerId, status).stream().map(orderMapper::orderToOrderDto).toList();
    }

    @Override
    public OrderDto completeOrder(Long orderId) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isPresent()){
            Order prevOrder = optOrder.get();

            for(OrderProduct orderProduct: prevOrder.getOrderProducts()){
                Product product = orderProduct.getProduct();
                Integer quantity = orderProduct.getQuantity();

                product.setStockQuantity(Math.max(product.getStockQuantity() - quantity, 0));
                productRepository.save(product);
            }

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
    public Order applyCoupon(Long orderId, String correlationId) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isPresent()) {
            Order order = optOrder.get();
            Coupon coupon = couponServiceProxy.getCoupon(correlationId).getBody();

            if(coupon == null){
                return order;
            }

            log.info(coupon.getVersionId());
            log.info("correlation-id coupon: {}", correlationId);

            BigDecimal newTotal = BigDecimal.ZERO;

            for(OrderProduct orderProduct: order.getOrderProducts()){
                Product product = productRepository.getReferenceById(orderProduct.getProduct().getProductId());
                orderProduct.getProduct().setPrice(product.getPrice());

                if(coupon.getProductCategory().toLowerCase().equals(orderProduct.getProduct().getCategory())){
                    double percentPaid = (100 - coupon.getDiscount()) / 100.0;

                    orderProduct.getProduct().setPrice(orderProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(percentPaid)));
                }

                int quantity = orderProduct.getQuantity();
                orderProduct.setPrice(orderProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));

                orderProductRepository.save(orderProduct);

                newTotal = newTotal.add(orderProduct.getPrice());
            }

            order.setTotalAmount(newTotal);

            return orderRepository.save(order);
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
    public void saveOrUpdateOrder(OrderDto orderDto) {
        orderRepository.save(orderMapper.orderDtoToOrder(orderDto));
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
    public void updateTotalAmount(Long orderId, BigDecimal amount) {
        Optional<Order> optOrder = orderRepository.findById(orderId);
        if (optOrder.isPresent()){
            Order prevOrder = optOrder.get();

            prevOrder.setTotalAmount(amount);
            prevOrder.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));

            Order savedOrder = orderRepository.save(prevOrder);
            orderMapper.orderToOrderDto(savedOrder);
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
