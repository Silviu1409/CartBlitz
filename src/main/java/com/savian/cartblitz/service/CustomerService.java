package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerDto> getAllCustomers();
    Optional<Customer> getCustomerById(Long customerId);
    Optional<Customer> getCustomerByUsername(String username);
    Optional<Customer> getCustomerByEmail(String email);

    List<CustomerDto> getCustomersAscFullName();
    List<CustomerDto> getCustomersDescFullName();

    Customer saveCustomer(CustomerDto customerDto);
    Customer updateCustomer(Long customerId, CustomerDto customerDto);
    void removeCustomerById(Long customerId);
}
