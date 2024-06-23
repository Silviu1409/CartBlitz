package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.mapper.CustomerMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mysql")
@org.junit.jupiter.api.Tag("test")
public class CustomerControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerMapper customerMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllCustomers() throws Exception {
        List<CustomerDto> customerDtoList = Arrays.asList(getDummyCustomerDtoOne(), getDummyCustomerDtoTwo());

        when(customerService.getAllCustomers()).thenReturn(customerDtoList);

        mockMvc.perform(get("/customer").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerDtoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.customerDtoList[0].customerId").value(customerDtoList.get(0).getCustomerId()))
                .andExpect(jsonPath("$._embedded.customerDtoList[1].customerId").value(customerDtoList.get(1).getCustomerId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomerByIdFound() throws Exception {
        CustomerDto customerDto = getDummyCustomerDtoOne();

        when(customerService.getCustomerById(any())).thenReturn(Optional.of(customerDto));

        mockMvc.perform(get("/customer/id/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(customerDto.getCustomerId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomerByIdNotFound() throws Exception {
        when(customerService.getCustomerById(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/customer/id/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomerByUsername() throws Exception {
        CustomerDto customerDto = getDummyCustomerDtoOne();

        when(customerService.getCustomerByUsername(any())).thenReturn(Optional.of(customerDto));

        mockMvc.perform(get("/customer/username/testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(customerDto.getCustomerId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomersAscFullName() throws Exception {
        List<CustomerDto> customerDtoList = Arrays.asList(getDummyCustomerDtoOne(), getDummyCustomerDtoTwo());

        when(customerService.getCustomersAscFullName()).thenReturn(customerDtoList);

        mockMvc.perform(get("/customer/asc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerDtoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.customerDtoList[0].customerId").value(customerDtoList.get(0).getCustomerId()))
                .andExpect(jsonPath("$._embedded.customerDtoList[1].customerId").value(customerDtoList.get(1).getCustomerId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetCustomersDescFullName() throws Exception {
        List<CustomerDto> customerDtoList = Arrays.asList(getDummyCustomerDtoTwo(), getDummyCustomerDtoOne());

        when(customerService.getCustomersDescFullName()).thenReturn(customerDtoList);

        mockMvc.perform(get("/customer/desc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.customerDtoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.customerDtoList[0].customerId").value(customerDtoList.get(0).getCustomerId()))
                .andExpect(jsonPath("$._embedded.customerDtoList[1].customerId").value(customerDtoList.get(1).getCustomerId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCustomerSuccess() throws Exception {
        Customer customer = getDummyCustomerOne();
        CustomerDto customerDto = getDummyCustomerDtoOne();

        when(customerService.saveCustomer(any())).thenReturn(customer);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/customer/" + customerDto.getCustomerId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCustomerInvalid() throws Exception {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();

        when(customerService.saveCustomer(any())).thenReturn(customer);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateCustomerAccessDenied() throws Exception {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();

        when(customerService.saveCustomer(any())).thenReturn(customer);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCustomerSuccess() throws Exception {
        Customer customer = getDummyCustomerOne();
        CustomerDto customerDto = getDummyCustomerDtoOne();

        when(customerService.updateCustomer(anyLong(), any())).thenReturn(customer);

        mockMvc.perform(put("/customer/id/{customerId}", customerDto.getCustomerId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(customerDto.getCustomerId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateCustomerInvalid() throws Exception {
        Customer customer = getDummyCustomerOne();
        CustomerDto customerDto = new CustomerDto();

        when(customerService.updateCustomer(anyLong(), any())).thenReturn(customer);

        mockMvc.perform(put("/customer/id/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateCustomerAccessDenied() throws Exception {
        Customer customer = getDummyCustomerOne();
        CustomerDto customerDto = new CustomerDto();

        when(customerService.updateCustomer(anyLong(), any())).thenReturn(customer);

        mockMvc.perform(put("/customer/id/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCustomerSuccess() throws Exception {
        mockMvc.perform(delete("/customer/id/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customerService, times(1)).removeCustomerById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteCustomerNotFound() throws Exception {
        Long customerId = 10L;

        doThrow(new CustomerNotFoundException(customerId)).when(customerService).removeCustomerById(customerId);

        mockMvc.perform(delete("/customer/id/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteCustomerAccessDenied() throws Exception {
        mockMvc.perform(delete("/customer/id/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Customer getDummyCustomerOne(){
        Customer customer = new Customer();
        customer.setCustomerId(10L);
        customer.setUsername("userTest1");
        customer.setPassword("718%BYYLo");
        customer.setEmail("test@test.com");
        customer.setFullName("User Test One");
        return customer;
    }

    private CustomerDto getDummyCustomerDtoOne(){
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(10L);
        customer.setUsername("userTest1");
        customer.setPassword("718%BYYLo");
        customer.setEmail("test@test.com");
        customer.setFullName("User Test One");
        return customer;
    }

    private CustomerDto getDummyCustomerDtoTwo(){
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(11L);
        customer.setUsername("userTest2");
        customer.setPassword("718%BYYLo");
        customer.setEmail("test2@test.com");
        customer.setFullName("User Test Two");
        return customer;
    }
}
