package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.exception.OrderProductNotFoundException;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.mapper.OrderProductMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.service.OrderProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
public class OrderProductControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderProductService orderProductService;

    @MockBean
    private OrderProductMapper orderProductMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllOrderProducts() throws Exception {
        OrderProduct orderProductOne = getDummyOrderProductOne();
        OrderProduct orderProductTwo = getDummyOrderProductTwo();
        List<OrderProduct> customerDtoList = Arrays.asList(orderProductOne, orderProductTwo);
        OrderProductId orderProductId = new OrderProductId(orderProductOne.getOrder().getOrderId(), orderProductOne.getProduct().getProductId());

        when(orderProductService.getAllOrderProducts()).thenReturn(customerDtoList);

        mockMvc.perform(get("/orderProduct")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.orderProductList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.orderProductList[0].orderProductId").value(orderProductId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderProductByIdSuccess() throws Exception {
        OrderProduct orderProduct = getDummyOrderProductOne();
        OrderProductId orderProductId = new OrderProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());

        when(orderProductService.getOrderProductByOrderIdAndProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId())).thenReturn(Optional.of(orderProduct));

        mockMvc.perform(get("/orderProduct/orderId/{orderId}/productId/{productId}", orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderProductId").value(orderProductId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderProductByIdNotFound() throws Exception {
        when(orderProductService.getOrderProductByOrderIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/orderProduct/orderId/1/productId/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderProductsByOrderIdSuccess() throws Exception {
        OrderProduct orderProductOne = getDummyOrderProductOne();
        OrderProductId orderProductId = new OrderProductId(orderProductOne.getOrder().getOrderId(), orderProductOne.getProduct().getProductId());

        when(orderProductService.getOrderProductsByOrderId(orderProductOne.getOrder().getOrderId())).thenReturn(List.of(orderProductOne));

        mockMvc.perform(get("/orderProduct/order/{orderId}", orderProductOne.getOrder().getOrderId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.orderProductList[0].orderProductId").value(orderProductId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderProductsByOrderIdNotFound() throws Exception {
        when(orderProductService.getOrderProductsByOrderId(99L)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/orderProduct/order/{orderId}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderProductsByProductIdSuccess() throws Exception {
        OrderProduct orderProductOne = getDummyOrderProductOne();
        OrderProduct orderProductTwo = getDummyOrderProductTwo();
        List<OrderProduct> customerDtoList = Arrays.asList(orderProductOne, orderProductTwo);
        OrderProductId orderProductId = new OrderProductId(orderProductOne.getOrder().getOrderId(), orderProductOne.getProduct().getProductId());

        when(orderProductService.getOrderProductsByProductId(orderProductOne.getProduct().getProductId())).thenReturn(customerDtoList);

        mockMvc.perform(get("/orderProduct/product/{productId}", orderProductOne.getProduct().getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.orderProductList[0].orderProductId").value(orderProductId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetOrderProductsByProductIdNotFound() throws Exception {
        when(orderProductService.getOrderProductsByProductId(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/orderProduct/product/{productId}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateOrderProductSuccess() throws Exception {
        OrderProduct orderProduct = getDummyOrderProductOne();
        OrderProductDto orderProductDto = getDummyOrderProductDtoOne();

        when(orderProductService.saveOrderProduct(any())).thenReturn(orderProduct);

        mockMvc.perform(post("/orderProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderProductDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orderProduct/orderId/" + orderProductDto.getOrderId() + "/productId/" + orderProductDto.getProductId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateOrderProductInvalid() throws Exception {
        OrderProductDto orderProductDto = getDummyOrderProductDtoOne();
        OrderProduct orderProduct = getDummyOrderProductOne();
        orderProductDto.setQuantity(0);
        orderProduct.setQuantity(0);

        when(orderProductMapper.orderProductDtoToOrderProduct(orderProductDto)).thenReturn(orderProduct);
        when(orderProductService.saveOrderProduct(orderProductDto)).thenReturn(orderProduct);

        mockMvc.perform(post("/orderProduct")
                        .content(objectMapper.writeValueAsString(orderProductDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateOrderProductAccessDenied() throws Exception {
        OrderProductDto orderProductDto = getDummyOrderProductDtoOne();
        orderProductDto.setQuantity(0);

        when(orderProductService.saveOrderProduct(orderProductDto)).thenReturn(new OrderProduct());

        mockMvc.perform(MockMvcRequestBuilders.post("/orderProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\": 10, \"productId\": 10}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteOrderProductSuccess() throws Exception {
        mockMvc.perform(delete("/orderProduct/orderId/{orderId}/productId/{productId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteOrderProductNotFound() throws Exception {
        doThrow(new OrderProductNotFoundException(1L, 1L)).when(orderProductService).removeOrderProductById(1L, 1L);

        mockMvc.perform(delete("/orderProduct/orderId/1/productId/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteOrderProductAccessDenied() throws Exception {
        mockMvc.perform(delete("/orderProduct/orderId/1/productId/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private OrderProduct getDummyOrderProductOne(){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(getDummyOrderOne());
        orderProduct.setProduct(getDummyProduct());
        orderProduct.setOrderProductId(new OrderProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId()));
        orderProduct.setQuantity(1);
        orderProduct.setPrice(BigDecimal.valueOf(0));
        return orderProduct;
    }

    private OrderProduct getDummyOrderProductTwo(){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(getDummyOrderTwo());
        orderProduct.setProduct(getDummyProduct());
        orderProduct.setOrderProductId(new OrderProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId()));
        orderProduct.setQuantity(1);
        orderProduct.setPrice(BigDecimal.valueOf(0));
        return orderProduct;
    }

    private OrderProductDto getDummyOrderProductDtoOne(){
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setOrderId(getDummyOrderOne().getOrderId());
        orderProductDto.setProductId(getDummyProduct().getProductId());
        orderProductDto.setQuantity(1);
        orderProductDto.setPrice(BigDecimal.valueOf(0));
        return orderProductDto;
    }

    private Order getDummyOrderOne(){
        Order order = new Order();
        order.setOrderId(10L);
        order.setCustomer(getDummyCustomer());
        order.setTotalAmount(BigDecimal.valueOf(0));
        order.setStatus(OrderStatusEnum.CART);
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        return order;
    }

    private Order getDummyOrderTwo(){
        Order order = new Order();
        order.setOrderId(11L);
        order.setCustomer(getDummyCustomer());
        order.setTotalAmount(BigDecimal.valueOf(0));
        order.setStatus(OrderStatusEnum.CART);
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        return order;
    }

    private Product getDummyProduct(){
        Product product = new Product();
        product.setProductId(10L);
        product.setName("productTest");
        product.setPrice(BigDecimal.valueOf(0L));
        product.setStockQuantity(0);
        product.setDescription("productTest description");
        product.setBrand("productTest brand");
        product.setCategory("productTest category");
        return product;
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
}
