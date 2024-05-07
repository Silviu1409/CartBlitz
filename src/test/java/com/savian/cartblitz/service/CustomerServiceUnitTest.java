package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.CustomerUsernameDuplicateException;
import com.savian.cartblitz.mapper.CustomerMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
@org.junit.jupiter.api.Tag("test")
public class CustomerServiceUnitTest {
    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;

    @Test
    public void testGetAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        customers.add(getDummyCustomer());

        log.info("Starting testGetAllCustomers");

        Mockito.when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDto> result = customerService.getAllCustomers();
        customers.forEach(customer -> log.info(customer.getUsername()));

        Mockito.verify(customerRepository).findAll();

        log.info("Finished testGetAllCustomers");

        Assertions.assertEquals(customers.stream().map(customerMapper::customerToCustomerDto).toList(), result);
    }

    @Test
    public void testGetCustomerByIdFound() {
        Customer customer = getDummyCustomer();

        log.info("Starting testGetCustomerByIdFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(customer.getCustomerId());
        result.ifPresent(value -> log.info(String.valueOf(value.getCustomerId())));

        Mockito.verify(customerRepository).findById(customer.getCustomerId());

        log.info("Finished testGetCustomerByIdFound successfully");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(customer, result.get());
    }

    @Test
    public void testGetCustomerByIdNotFound() {
        Customer customer = getDummyCustomer();

        log.info("Starting testGetCustomerByIdNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customer.getCustomerId()));
        log.error("Customer with given ID was not found");

        Mockito.verify(customerRepository).findById(customer.getCustomerId());

        log.info("Finished testGetCustomerByIdNotFound successfully");
    }

    @Test
    public void testGetCustomerByUsernameFound() {
        Customer customer = getDummyCustomer();

        log.info("Starting testGetCustomerByUsernameFound");

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerByUsername("userTest");

        result.ifPresent(value -> log.info(value.getUsername()));

        Mockito.verify(customerRepository).findByUsername("userTest");

        log.info("Finished testGetCustomerByUsernameFound successfully");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(customer, result.get());
    }

    @Test
    public void testGetCustomerByUsernameNotFound() {
        log.info("Starting testGetCustomerByUsernameNotFound");

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerByUsername("userTest"));
        log.error("Customer with given username was not found");

        Mockito.verify(customerRepository).findByUsername("userTest");

        log.info("Finished testGetCustomerByUsernameNotFound successfully");
    }

    @Test
    public void testGetCustomersAscFullName() {
        List<Customer> customers = new ArrayList<>();
        customers.add(getDummyCustomer());

        log.info("Starting testGetCustomersAscFullName");

        Mockito.when(customerRepository.findAllByOrderByFullNameAsc()).thenReturn(customers);

        List<CustomerDto> result = customerService.getCustomersAscFullName();
        customers.forEach(customer -> log.info(customer.getFullName()));

        Mockito.verify(customerRepository).findAllByOrderByFullNameAsc();

        log.info("Finished testGetCustomersAscFullName successfully");

        Assertions.assertEquals(customers.stream().map(customerMapper::customerToCustomerDto).toList(), result);
    }

    @Test
    public void testGetCustomersDescFullName() {
        List<Customer> customers = new ArrayList<>();
        customers.add(getDummyCustomer());

        log.info("Starting testGetCustomersDescFullName");

        Mockito.when(customerRepository.findAllByOrderByFullNameDesc()).thenReturn(customers);

        List<CustomerDto> result = customerService.getCustomersDescFullName();
        customers.forEach(customer -> log.info(customer.getFullName()));

        Mockito.verify(customerRepository).findAllByOrderByFullNameDesc();

        log.info("Finished testGetCustomersDescFullName successfully");

        Assertions.assertEquals(customers.stream().map(customerMapper::customerToCustomerDto).toList(), result);
    }

    @Test
    public void testSaveCustomerSuccess() {
        CustomerDto customerDto = getDummyCustomerDto();
        Customer savedCustomerEntity = getDummyCustomer();

        log.info("Starting testSaveCustomerSuccess");

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(customerRepository.save(customerMapper.customerDtoToCustomer(Mockito.any(CustomerDto.class)))).thenReturn(savedCustomerEntity);

        Customer result = customerService.saveCustomer(customerDto);
        log.info(result.getUsername());

        Mockito.verify(customerRepository).findByUsername(customerDto.getUsername());
        Mockito.verify(customerRepository).save(customerMapper.customerDtoToCustomer(customerDto));

        log.info("Finished testSaveCustomerSuccess successfully");

        Assertions.assertEquals(savedCustomerEntity, result);
    }

    @Test
    public void testSaveCustomerUsernameDuplicate() {
        CustomerDto customerDto = getDummyCustomerDto();
        Customer duplicateCustomer = getDummyCustomer();

        log.info("Starting testSaveCustomerUsernameDuplicate");

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(duplicateCustomer));

        Assertions.assertThrows(CustomerUsernameDuplicateException.class, () -> customerService.saveCustomer(customerDto));

        log.error("A customer with the same username already exists");

        Mockito.verify(customerRepository).findByUsername(Mockito.anyString());

        log.info("Finished testSaveCustomerUsernameDuplicate successfully");
    }

    @Test
    public void testUpdateCustomerSuccess() {
        Customer existingCustomer = getDummyCustomer();

        log.info("Starting testUpdateCustomerSuccess");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingCustomer));
        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Customer updatedCustomer = getDummyCustomer();
        CustomerDto updatedCustomerDto = getDummyCustomerDto();

        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = customerService.updateCustomer(existingCustomer.getCustomerId(), updatedCustomerDto);

        log.info(result.getUsername());

        Mockito.verify(customerRepository).findById(existingCustomer.getCustomerId());
        Mockito.verify(customerRepository).findByUsername(updatedCustomerDto.getUsername());
        Mockito.verify(customerRepository).save(updatedCustomer);

        log.info("Finished testUpdateCustomerSuccess successfully");

        Assertions.assertEquals(updatedCustomer, result);
    }

    @Test
    public void testUpdateCustomerNotFound() {
        CustomerDto customerDto = getDummyCustomerDto();

        log.info("Starting testUpdateCustomerNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customerDto.getCustomerId(), customerDto));
        log.error("Existing customer was not found");

        Mockito.verify(customerRepository).findById(customerDto.getCustomerId());

        log.info("Finished testUpdateCustomerNotFound successfully");
    }

    @Test
    public void testUpdateCustomerUsernameDuplicate() {
        CustomerDto customerDto = getDummyCustomerDto();
        Customer existingCustomer = getDummyCustomer();
        Customer duplicateCustomer = getDummyCustomer();

        duplicateCustomer.setCustomerId(11L);
        duplicateCustomer.setUsername("userTestDuplicate");
        customerDto.setUsername("userTestDuplicate");

        log.info("Starting testUpdateCustomerUsernameDuplicate");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingCustomer));

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(duplicateCustomer));

        Assertions.assertThrows(CustomerUsernameDuplicateException.class, () -> customerService.updateCustomer(existingCustomer.getCustomerId(), customerDto));
        log.error("A customer with the same username already exists");

        Mockito.verify(customerRepository).findById(existingCustomer.getCustomerId());
        Mockito.verify(customerRepository).findByUsername(customerDto.getUsername());

        log.info("Finished testUpdateCustomerUsernameDuplicate successfully");
    }

    @Test
    public void testRemoveCustomerByIdSuccess() {
        Customer customer = getDummyCustomer();

        log.info("Starting testRemoveCustomerByIdSuccess");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(customer));

        customerService.removeCustomerById(customer.getCustomerId());
        log.info(String.valueOf(customer.getCustomerId()));

        Mockito.verify(customerRepository).findById(customer.getCustomerId());
        Mockito.verify(customerRepository).deleteById(customer.getCustomerId());

        log.info("Finished testRemoveCustomerByIdSuccess successfully");
    }

    @Test
    public void testRemoveCustomerByIdNotFound() {
        Customer customer = getDummyCustomer();

        log.info("Starting testRemoveCustomerByIdNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.removeCustomerById(customer.getCustomerId()));
        log.error("Customer with given ID was not found");

        Mockito.verify(customerRepository).findById(customer.getCustomerId());

        log.info("Finished testRemoveCustomerByIdNotFound successfully");
    }

    private Customer getDummyCustomer(){
        Customer customer = new Customer();
        customer.setCustomerId(10L);
        customer.setUsername("userTest");
        customer.setPassword("718%BYYLo");
        customer.setEmail("test@test.com");
        customer.setFullName("User Test");
        return customer;
    }

    private CustomerDto getDummyCustomerDto(){
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(10L);
        customer.setUsername("userTest");
        customer.setPassword("718%BYYLo");
        customer.setEmail("test@test.com");
        customer.setFullName("User Test");
        return customer;
    }
}
