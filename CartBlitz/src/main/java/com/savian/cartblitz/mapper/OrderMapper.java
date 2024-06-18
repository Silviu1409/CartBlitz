package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.OrderProductRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    private final OrderProductMapper orderProductMapper;
    private final OrderProductRepository orderProductRepository;
    private final CustomerRepository customerRepository;

    public OrderMapper(OrderProductMapper orderProductMapper, OrderProductRepository orderProductRepository, CustomerRepository customerRepository) {
        this.orderProductMapper = orderProductMapper;
        this.orderProductRepository = orderProductRepository;
        this.customerRepository = customerRepository;
    }

    public OrderDto orderToOrderDto(Order order){
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setCustomerId(order.getCustomer().getCustomerId());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setStatus(order.getStatus());
        orderDto.setOrderDate(order.getOrderDate());
        if (order.getOrderProducts() != null){
            orderDto.setOrderProducts(order.getOrderProducts().stream().map(orderProductMapper::orderProductToOrderProductDto).toList());
        }
        return orderDto;
    }

    public Order orderDtoToOrder(OrderDto orderDto){
        Order order = new Order();
        order.setOrderId(orderDto.getOrderId());
        order.setCustomer(customerRepository.getReferenceById(orderDto.getCustomerId()));
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setStatus(orderDto.getStatus());
        order.setOrderDate(orderDto.getOrderDate());
        order.setOrderProducts(orderProductRepository.findByOrderOrderId(orderDto.getOrderId()));
        return order;
    }
}
