package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ReviewRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    private final OrderMapper orderMapper;
    private final ReviewMapper reviewMapper;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    public CustomerMapper(OrderMapper orderMapper, ReviewMapper reviewMapper, OrderRepository orderRepository, ReviewRepository reviewRepository) {
        this.orderMapper = orderMapper;
        this.reviewMapper = reviewMapper;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    public Customer customerDtoToCustomer(CustomerDto customerDto){
        Customer customer = new Customer();
        customer.setCustomerId(customerDto.getCustomerId());
        customer.setUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        customer.setEmail(customerDto.getEmail());
        customer.setFullName(customerDto.getFullName());
        customer.setOrders(orderRepository.findByCustomerCustomerId(customerDto.getCustomerId()));
        customer.setReviews(reviewRepository.findByCustomerCustomerId(customerDto.getCustomerId()));
        return customer;
    }

    public CustomerDto customerToCustomerDto(Customer customer){
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(customer.getCustomerId());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setEmail(customer.getEmail());
        customerDto.setFullName(customer.getFullName());
        customerDto.setOrders(customer.getOrders().stream().map(orderMapper::orderToOrderDto).toList());
        customerDto.setReviews(customer.getReviews().stream().map(reviewMapper::reviewToReviewDto).toList());
        return customerDto;
    }
}
