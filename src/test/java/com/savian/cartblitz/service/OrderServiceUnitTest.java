package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.OrderInProgressException;
import com.savian.cartblitz.exception.OrderNotFoundException;
import com.savian.cartblitz.mapper.OrderMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.OrderStatusEnum;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testGetAllOrders() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getAllOrders();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderDto, result.get(0));
    }

    @Test
    void testGetOrderByIdFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.orderToOrderDto(Mockito.any(Order.class))).thenReturn(orderDto);

        Optional<OrderDto> result = orderService.getOrderById(orderDto.getOrderId());

        Mockito.verify(orderRepository).findById(order.getOrderId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(orderDto, result.get());
    }

    @Test
    void testGetOrderByIdNotFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(order.getOrderId()));
    }

    @Test
    void testGetOrdersByCustomerIdFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order.getCustomer()));
        Mockito.when(orderRepository.findByCustomerCustomerId(Mockito.anyLong())).thenReturn(Collections.singletonList(order));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getOrdersByCustomerId(orderDto.getCustomerId());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderDto, result.get(0));
    }

    @Test
    void testGetOrdersByCustomerIdCustomerNotFound() {
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> orderService.getOrdersByCustomerId(orderDto.getCustomerId()));
    }

    @Test
    void testGetOrdersByStatus() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findByStatus(Mockito.any())).thenReturn(Collections.singletonList(order));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.gelOrdersByStatus(order.getStatus());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderDto, result.get(0));
    }

    @Test
    void testCompleteOrderFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.completeOrder(order.getOrderId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatusEnum.COMPLETED, order.getStatus());
    }

    @Test
    void testCompleteOrderNotFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.completeOrder(order.getOrderId()));
    }

    @Test
    void testModifyTotalAmountFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.modifyTotalAmount(order.getOrderId(), BigDecimal.valueOf(10));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(BigDecimal.valueOf(10), order.getTotalAmount());
    }

    @Test
    void testModifyTotalAmountNotFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.modifyTotalAmount(order.getOrderId(), BigDecimal.TEN));
    }

    @Test
    void testSaveOrder() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findByCustomerCustomerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order.getCustomer()));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.saveOrder(order.getOrderId());

        Assertions.assertNotNull(result);
    }

    @Test
    void testSaveOrderInProgressFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findByCustomerCustomerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(Collections.singletonList(getDummyOrder()));

        Assertions.assertThrows(OrderInProgressException.class, () -> orderService.saveOrder(order.getOrderId()));
    }

    @Test
    void testSaveOrderCustomerNotFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findByCustomerCustomerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> orderService.saveOrder(order.getOrderId()));
    }

    @Test
    void testUpdateOrderFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order.getCustomer()));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.updateOrder(order.getOrderId(), order.getOrderId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(order.getOrderId(), order.getCustomer().getCustomerId());

        Mockito.verify(orderMapper).orderToOrderDto(order);
    }

    @Test
    void testUpdateOrderNotFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(order.getOrderId(), order.getOrderId()));
    }

    @Test
    void testRemoveOrderByIdFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));

        Assertions.assertDoesNotThrow(() -> orderService.removeOrderById(order.getOrderId()));

        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void testRemoveOrderByIdNotFound() {
        Order order = getDummyOrder();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.removeOrderById(order.getOrderId()));
    }

    private Order getDummyOrder(){
        Order order = new Order();
        order.setOrderId(10L);
        order.setCustomer(getDummyCustomer());
        order.setTotalAmount(BigDecimal.valueOf(0));
        order.setStatus(OrderStatusEnum.CART);
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        return order;
    }

    private OrderDto getDummyOrderDto(){
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(10L);
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
