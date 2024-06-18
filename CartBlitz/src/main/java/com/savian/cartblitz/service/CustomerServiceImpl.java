package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.exception.CustomerEmailDuplicateException;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.CustomerUsernameDuplicateException;
import com.savian.cartblitz.mapper.CustomerMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream().map(customerMapper::customerToCustomerDto).toList();
    }

    @Override
    public Optional<Customer> getCustomerById(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            return customer;
        }
        else {
            throw new CustomerNotFoundException(customerId);
        }
    }

    @Override
    public Optional<Customer> getCustomerByUsername(String username) {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            return customer;
        }
        else {
            throw new CustomerNotFoundException("username", username);
        }
    }

    @Override
    public List<CustomerDto> getCustomersAscFullName() {
        return customerRepository.findAllByOrderByFullNameAsc().stream().map(customerMapper::customerToCustomerDto).toList();
    }

    @Override
    public List<CustomerDto> getCustomersDescFullName() {
        return customerRepository.findAllByOrderByFullNameDesc().stream().map(customerMapper::customerToCustomerDto).toList();
    }

    @Override
    public Customer saveCustomer(CustomerDto customerDto) {
        Optional<Customer> existingCustomer = customerRepository.findByUsername(customerDto.getUsername());

        if (existingCustomer.isPresent()) {
            throw new CustomerUsernameDuplicateException();
        } else {
            existingCustomer = customerRepository.findByEmail(customerDto.getEmail());

            if (existingCustomer.isPresent()) {
                throw new CustomerEmailDuplicateException();
            } else {
                return customerRepository.save(customerMapper.customerDtoToCustomer(customerDto));
            }
        }
    }

    @Override
    public Customer updateCustomer(Long customerId, CustomerDto updatedCustomer) {
        Optional<Customer> optCustomer = customerRepository.findById(customerId);

        if (optCustomer.isPresent()) {
            Optional<Customer> duplicateCustomer = customerRepository.findByUsername(updatedCustomer.getUsername());

            if (duplicateCustomer.isPresent() && !Objects.equals(duplicateCustomer.get().getCustomerId(), customerId)) {
                throw new CustomerUsernameDuplicateException();
            }

            duplicateCustomer = customerRepository.findByEmail(updatedCustomer.getEmail());

            if (duplicateCustomer.isPresent() && !Objects.equals(duplicateCustomer.get().getCustomerId(), customerId)) {
                throw new CustomerEmailDuplicateException();
            }

            Customer existingCustomer = optCustomer.get();

            existingCustomer.setUsername(updatedCustomer.getUsername());
            existingCustomer.setPassword(updatedCustomer.getPassword());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setFullName(updatedCustomer.getFullName());

            return customerRepository.save(existingCustomer);
        } else {
            throw new CustomerNotFoundException(customerId);
        }
    }

    @Override
    public void removeCustomerById(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);

        if(customer.isPresent()){
            customerRepository.deleteById(customerId);
        }
        else{
            throw new CustomerNotFoundException(customerId);
        }
    }
}
