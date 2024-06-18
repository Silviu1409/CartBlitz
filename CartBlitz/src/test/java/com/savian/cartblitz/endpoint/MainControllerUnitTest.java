package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.controller.CategoryController;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.security.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mysql")
@org.junit.jupiter.api.Tag("test")
public class MainControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryController categoryController;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private AuthorityRepository authorityRepository;

    @Test
    public void testGetHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(view().name("main"));
    }

    @Test
    public void testAccessDeniedPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accessDenied"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testShowLogInForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testShowRegisterForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("customer"));
    }

    @Test
    public void testProcessRegister_ValidationFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .param("username", "testuser")
                        .param("password", "password")
                        .param("email", "test@example.com")
                        .param("fullName", "Test User"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testViewProfile_CustomerFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("testuser");

        Customer customer = new Customer();
        customer.setUsername("testuser");
        List<Order> completedOrders = new ArrayList<>();
        customer.setOrders(completedOrders);
        Optional<Customer> optionalCustomer = Optional.of(customer);

        Mockito.when(customerRepository.findByUsername("testuser")).thenReturn(optionalCustomer);

        mockMvc.perform(MockMvcRequestBuilders.get("/profile").principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testViewProfile_CustomerNotFound() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("nonexistentuser");

        Mockito.when(customerRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/profile").principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testShowProductAdd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addProduct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testShowCategories() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", categoryController.getCategoryMap()));
    }
}
