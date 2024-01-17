package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.exception.OrderNotFoundException;
import com.savian.cartblitz.mapper.OrderMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.OrderStatusEnum;
import com.savian.cartblitz.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderMapper orderMapper;

    @Test
    void getAllOrders() throws Exception {
        OrderDto orderDtoOne = getDummyOrderDtoOne();
        OrderDto orderDtoTwo = getDummyOrderDtoTwo();
        List<OrderDto> orders = List.of(orderDtoOne, orderDtoTwo);

        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/order")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(orderDtoOne.getOrderId()))
                .andExpect(jsonPath("$[1].orderId").value(orderDtoTwo.getOrderId()));
    }

    @Test
    void getOrderByIdFound() throws Exception {
        OrderDto orderDto = getDummyOrderDtoOne();

        when(orderService.getOrderById(orderDto.getOrderId())).thenReturn(Optional.of(orderDto));

        mockMvc.perform(get("/order/id/{orderId}", orderDto.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderDto.getOrderId()));
    }

    @Test
    void getOrderByIdNotFound() throws Exception {
        when(orderService.getOrderById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/order/id/{orderId}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersByCustomerId() throws Exception {
        OrderDto orderDtoOne = getDummyOrderDtoOne();
        OrderDto orderDtoTwo = getDummyOrderDtoTwo();
        List<OrderDto> orders = List.of(orderDtoOne, orderDtoTwo);

        when(orderService.getOrdersByCustomerId(orderDtoOne.getCustomerId())).thenReturn(orders);

        mockMvc.perform(get("/order/customer/{customerId}", orderDtoOne.getCustomerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(orderDtoOne.getOrderId()))
                .andExpect(jsonPath("$[1].orderId").value(orderDtoTwo.getOrderId()));
    }

    @Test
    void getOrdersByStatus() throws Exception {
        OrderDto orderDtoOne = getDummyOrderDtoOne();
        OrderDto orderDtoTwo = getDummyOrderDtoTwo();
        List<OrderDto> orders = List.of(orderDtoOne, orderDtoTwo);

        when(orderService.gelOrdersByStatus(OrderStatusEnum.CART)).thenReturn(orders);

        mockMvc.perform(get("/order/status/{status}", "CART")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(orderDtoOne.getOrderId()))
                .andExpect(jsonPath("$[1].orderId").value(orderDtoTwo.getOrderId()));
    }

    @Test
    void completeOrderFound() throws Exception {
        OrderDto orderDto = getDummyOrderDtoOne();

        when(orderService.completeOrder(orderDto.getOrderId())).thenReturn(orderDto);

        mockMvc.perform(get("/order/complete/{orderId}", orderDto.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderDto.getOrderId()));
    }

    @Test
    void modifyTotalAmountSuccess() throws Exception {
        OrderDto orderDto = getDummyOrderDtoOne();

        when(orderService.modifyTotalAmount(orderDto.getOrderId(), BigDecimal.TEN)).thenReturn(orderDto);

        mockMvc.perform(get("/order/modify")
                        .param("orderId", String.valueOf(orderDto.getOrderId()))
                        .param("amount", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderDto.getOrderId()));
    }

    @Test
    void createOrderSuccess() throws Exception {
        OrderDto orderDto = getDummyOrderDtoOne();

        when(orderService.saveOrder(orderDto.getOrderId())).thenReturn(orderDto);

        mockMvc.perform(post("/order")
                        .param("customerId", orderDto.getCustomerId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderDto.getOrderId()));
    }

    @Test
    void updateOrderSuccess() throws Exception {
        OrderDto orderDto = getDummyOrderDtoOne();

        when(orderService.updateOrder(orderDto.getOrderId(), orderDto.getCustomerId())).thenReturn(orderDto);

        mockMvc.perform(put("/order/id/{orderId}", orderDto.getOrderId())
                        .param("customerId", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderDto.getOrderId()));
    }

    @Test
    void deleteOrderFound() throws Exception {
        mockMvc.perform(delete("/order/id/{orderId}", 10)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderService, times(1)).removeOrderById(10L);
    }

    @Test
    void deleteOrderNotFound() throws Exception {
        doThrow(new OrderNotFoundException(99L)).when(orderService).removeOrderById(99L);

        mockMvc.perform(delete("/order/id/{orderId}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    private OrderDto getDummyOrderDtoOne(){
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(10L);
        orderDto.setCustomerId(getDummyCustomer().getCustomerId());
        orderDto.setTotalAmount(BigDecimal.valueOf(0));
        orderDto.setStatus(OrderStatusEnum.CART);
        orderDto.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        return orderDto;
    }

    private OrderDto getDummyOrderDtoTwo(){
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(11L);
        orderDto.setCustomerId(getDummyCustomer().getCustomerId());
        orderDto.setTotalAmount(BigDecimal.valueOf(0));
        orderDto.setStatus(OrderStatusEnum.CART);
        orderDto.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        return orderDto;
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
