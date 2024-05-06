package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.CustomerUsernameDuplicateException;
import com.savian.cartblitz.mapper.CustomerMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.repository.CustomerRepository;
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

        Mockito.when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDto> result = customerService.getAllCustomers();

        Mockito.verify(customerRepository).findAll();
        Assertions.assertEquals(customers.stream().map(customerMapper::customerToCustomerDto).toList(), result);
    }

    @Test
    public void testGetCustomerByIdFound() {
        Customer customer = getDummyCustomer();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(customer.getCustomerId());

        Mockito.verify(customerRepository).findById(customer.getCustomerId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(customer, result.get());
    }

    @Test
    public void testGetCustomerByIdNotFound() {
        Customer customer = getDummyCustomer();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customer.getCustomerId()));

        Mockito.verify(customerRepository).findById(customer.getCustomerId());
    }

    @Test
    public void testGetCustomerByUsernameFound() {
        Customer customer = getDummyCustomer();

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerByUsername("userTest");

        Mockito.verify(customerRepository).findByUsername("userTest");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(customer, result.get());
    }

    @Test
    public void testGetCustomerByUsernameNotFound() {
        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerByUsername("userTest"));

        Mockito.verify(customerRepository).findByUsername("userTest");
    }

    @Test
    public void testGetCustomersAscFullName() {
        List<Customer> customers = new ArrayList<>();
        customers.add(getDummyCustomer());

        Mockito.when(customerRepository.findAllByOrderByFullNameAsc()).thenReturn(customers);

        List<CustomerDto> result = customerService.getCustomersAscFullName();

        Mockito.verify(customerRepository).findAllByOrderByFullNameAsc();
        Assertions.assertEquals(customers.stream().map(customerMapper::customerToCustomerDto).toList(), result);
    }

    @Test
    public void testGetCustomersDescFullName() {
        List<Customer> customers = new ArrayList<>();
        customers.add(getDummyCustomer());

        Mockito.when(customerRepository.findAllByOrderByFullNameDesc()).thenReturn(customers);

        List<CustomerDto> result = customerService.getCustomersDescFullName();

        Mockito.verify(customerRepository).findAllByOrderByFullNameDesc();
        Assertions.assertEquals(customers.stream().map(customerMapper::customerToCustomerDto).toList(), result);
    }

    @Test
    public void testSaveCustomerSuccess() {
        CustomerDto customerDto = getDummyCustomerDto();
        Customer savedCustomerEntity = getDummyCustomer();

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(customerRepository.save(customerMapper.customerDtoToCustomer(Mockito.any(CustomerDto.class)))).thenReturn(savedCustomerEntity);

        Customer result = customerService.saveCustomer(customerDto);

        Mockito.verify(customerRepository).findByUsername(customerDto.getUsername());
        Mockito.verify(customerRepository).save(customerMapper.customerDtoToCustomer(customerDto));
        Assertions.assertEquals(savedCustomerEntity, result);
    }

    @Test
    public void testSaveCustomerUsernameDuplicate() {
        CustomerDto customerDto = getDummyCustomerDto();
        Customer duplicateCustomer = getDummyCustomer();

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(duplicateCustomer));

        Assertions.assertThrows(CustomerUsernameDuplicateException.class, () -> customerService.saveCustomer(customerDto));

        Mockito.verify(customerRepository).findByUsername(Mockito.anyString());
    }

    @Test
    public void testUpdateCustomerSuccess() {
        Customer existingCustomer = getDummyCustomer();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingCustomer));
        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Customer updatedCustomer = getDummyCustomer();
        CustomerDto updatedCustomerDto = getDummyCustomerDto();

        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(updatedCustomer);

        Customer result = customerService.updateCustomer(existingCustomer.getCustomerId(), updatedCustomerDto);

        Mockito.verify(customerRepository).findById(existingCustomer.getCustomerId());
        Mockito.verify(customerRepository).findByUsername(updatedCustomerDto.getUsername());
        Mockito.verify(customerRepository).save(updatedCustomer);
        Assertions.assertEquals(updatedCustomer, result);
    }

    @Test
    public void testUpdateCustomerNotFound() {
        CustomerDto customerDto = getDummyCustomerDto();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customerDto.getCustomerId(), customerDto));

        Mockito.verify(customerRepository).findById(customerDto.getCustomerId());
    }

    @Test
    public void testUpdateCustomerUsernameDuplicate() {
        CustomerDto customerDto = getDummyCustomerDto();
        Customer existingCustomer = getDummyCustomer();
        Customer duplicateCustomer = getDummyCustomer();

        duplicateCustomer.setCustomerId(11L);
        duplicateCustomer.setUsername("userTestDuplicate");
        customerDto.setUsername("userTestDuplicate");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingCustomer));

        Mockito.when(customerRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(duplicateCustomer));

        Assertions.assertThrows(CustomerUsernameDuplicateException.class, () -> customerService.updateCustomer(existingCustomer.getCustomerId(), customerDto));

        Mockito.verify(customerRepository).findById(existingCustomer.getCustomerId());
        Mockito.verify(customerRepository).findByUsername(customerDto.getUsername());
    }

    @Test
    public void testRemoveCustomerByIdSuccess() {
        Customer customer = getDummyCustomer();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(customer));

        customerService.removeCustomerById(customer.getCustomerId());

        Mockito.verify(customerRepository).findById(customer.getCustomerId());
        Mockito.verify(customerRepository).deleteById(customer.getCustomerId());
    }

    @Test
    public void testRemoveCustomerByIdNotFound() {
        Customer customer = getDummyCustomer();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> customerService.removeCustomerById(customer.getCustomerId()));

        Mockito.verify(customerRepository).findById(customer.getCustomerId());
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
