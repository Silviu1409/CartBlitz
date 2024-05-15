package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.OrderInProgressException;
import com.savian.cartblitz.exception.OrderNotFoundException;
import com.savian.cartblitz.mapper.OrderMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
@org.junit.jupiter.api.Tag("test")
public class OrderServiceUnitTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    @Test
    void testGetAllOrders() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testGetAllOrders");

        Mockito.when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getAllOrders();
        log.info(String.valueOf(order.getOrderId()));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderDto, result.get(0));

        log.info("Finished testGetAllOrders successfully");
    }

    @Test
    void testGetOrderByIdFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testGetOrderByIdFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.orderToOrderDto(Mockito.any(Order.class))).thenReturn(orderDto);

        Optional<OrderDto> result = orderService.getOrderById(orderDto.getOrderId());
        result.ifPresent(value -> log.info(String.valueOf(value.getOrderId())));

        Mockito.verify(orderRepository).findById(order.getOrderId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(orderDto, result.get());

        log.info("Finished testGetOrderByIdFound successfully");
    }

    @Test
    void testGetOrderByIdNotFound() {
        Order order = getDummyOrder();

        log.info("Starting testGetOrderByIdNotFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(order.getOrderId()));
        log.error("Order with given ID was not found");

        log.info("Finished testGetOrderByIdNotFound successfully");
    }

    @Test
    void testGetOrdersByCustomerIdFound() {
        Order order = getDummyOrder();
        Customer customer = getDummyCustomer();
        customer.setOrders(List.of(order));
        order.setCustomer(customer);

        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testGetOrdersByCustomerIdFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order.getCustomer()));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getOrdersByCustomerId(orderDto.getCustomerId());
        log.info(String.valueOf(order.getOrderId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderDto, result.get(0));

        Mockito.verify(customerRepository).findById(customer.getCustomerId());
        Mockito.verify(orderMapper).orderToOrderDto(order);

        log.info("Finished testGetOrdersByCustomerIdFound successfully");
    }

    @Test
    void testGetOrdersByCustomerIdCustomerNotFound() {
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testGetOrdersByCustomerIdCustomerNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        List<OrderDto> result =  orderService.getOrdersByCustomerId(orderDto.getCustomerId());
        log.error("Customer with given ID was not found, so an empty list has been returned");

        Assertions.assertTrue(result.isEmpty());

        log.info("Finished testGetOrdersByCustomerIdCustomerNotFound successfully");
    }

    @Test
    void testGetOrdersByStatus() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testGetOrdersByStatus");

        Mockito.when(orderRepository.findByStatus(Mockito.any())).thenReturn(Collections.singletonList(order));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getOrdersByStatus(order.getStatus());
        log.info(String.valueOf(order.getOrderId()));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(orderDto, result.get(0));

        log.info("Finished testGetOrdersByStatus successfully");
    }

    @Test
    void testCompleteOrderFound() {
        Order order = getDummyOrder();
        order.setOrderProducts(List.of(getDummyOrderProduct()));
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testCompleteOrderFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(orderRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.completeOrder(order.getOrderId());
        log.info(String.valueOf(result.getOrderId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatusEnum.COMPLETED, order.getStatus());

        Mockito.verify(orderRepository).findById(order.getOrderId());
        Mockito.verify(productRepository, Mockito.times(order.getOrderProducts().size())).save(Mockito.any());
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(orderMapper).orderToOrderDto(order);

        log.info("Finished testCompleteOrderFound successfully");
    }

    @Test
    void testCompleteOrderNotFound() {
        Order order = getDummyOrder();

        log.info("Starting testCompleteOrderNotFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.completeOrder(order.getOrderId()));
        log.error("Order with given ID was not found");

        log.info("Finished testCompleteOrderNotFound successfully");
    }

    @Test
    void testModifyTotalAmountFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testModifyTotalAmountFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.modifyTotalAmount(order.getOrderId(), BigDecimal.valueOf(10));
        log.info(String.valueOf(result.getOrderId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(BigDecimal.valueOf(10), order.getTotalAmount());

        log.info("Finished testModifyTotalAmountFound successfully");
    }

    @Test
    void testModifyTotalAmountNotFound() {
        Order order = getDummyOrder();

        log.info("Starting testModifyTotalAmountNotFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.modifyTotalAmount(order.getOrderId(), BigDecimal.TEN));
        log.error("Order with given ID was not found");

        log.info("Finished testModifyTotalAmountNotFound successfully");
    }

    @Test
    void testSaveOrder() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testSaveOrder");

        Mockito.when(orderRepository.findByCustomerCustomerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order.getCustomer()));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.saveOrder(order.getOrderId());
        log.info(String.valueOf(result.getOrderId()));

        Assertions.assertNotNull(result);

        log.info("Finished testSaveOrder successfully");
    }

    @Test
    void testSaveOrderInProgressFound() {
        Order order = getDummyOrder();

        log.info("Starting testSaveOrderInProgressFound");

        Mockito.when(orderRepository.findByCustomerCustomerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(Collections.singletonList(getDummyOrder()));

        Assertions.assertThrows(OrderInProgressException.class, () -> orderService.saveOrder(order.getOrderId()));
        log.error("An order with the given ID is already in progress");

        log.info("Finished testSaveOrderInProgressFound successfully");
    }

    @Test
    void testSaveOrderCustomerNotFound() {
        Order order = getDummyOrder();

        log.info("Starting testSaveOrderCustomerNotFound");

        Mockito.when(orderRepository.findByCustomerCustomerIdAndStatus(Mockito.anyLong(), Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> orderService.saveOrder(order.getOrderId()));
        log.error("Customer with given ID was not found");

        log.info("Finished testSaveOrderCustomerNotFound successfully");
    }

    @Test
    void testSaveOrUpdateOrder() {
        OrderDto orderDto = getDummyOrderDto();
        Order order = getDummyOrder();

        log.info("Starting testSaveOrUpdateOrder");

        Mockito.when(orderMapper.orderDtoToOrder(orderDto)).thenReturn(order);

        orderService.saveOrUpdateOrder(orderDto);
        log.info(String.valueOf(orderDto.getOrderId()));

        Mockito.verify(orderRepository).save(order);

        log.info("Finished testSaveOrUpdateOrder successfully");
    }

    @Test
    void testUpdateOrderFound() {
        Order order = getDummyOrder();
        OrderDto orderDto = getDummyOrderDto();

        log.info("Starting testUpdateOrderFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order.getCustomer()));
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);
        Mockito.when(orderMapper.orderToOrderDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.updateOrder(order.getOrderId(), order.getOrderId());
        log.info(String.valueOf(result.getOrderId()));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(order.getOrderId(), order.getCustomer().getCustomerId());

        Mockito.verify(orderMapper).orderToOrderDto(order);

        log.info("Finished testUpdateOrderFound successfully");
    }

    @Test
    void testUpdateOrderNotFound() {
        Order order = getDummyOrder();

        log.info("Starting testUpdateOrderNotFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(order.getOrderId(), order.getOrderId()));
        log.error("Order with given ID was not found");

        log.info("Finished testUpdateOrderNotFound successfully");
    }

    @Test
    void testRemoveOrderByIdFound() {
        Order order = getDummyOrder();

        log.info("Starting testRemoveOrderByIdFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));

        Assertions.assertDoesNotThrow(() -> orderService.removeOrderById(order.getOrderId()));
        log.info(String.valueOf(order.getOrderId()));

        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(Mockito.anyLong());

        log.info("Finished testRemoveOrderByIdFound successfully");
    }

    @Test
    void testRemoveOrderByIdNotFound() {
        Order order = getDummyOrder();

        log.info("Starting testRemoveOrderByIdNotFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.removeOrderById(order.getOrderId()));
        log.error("Order with given ID was not found");

        log.info("Finished testRemoveOrderByIdNotFound successfully");
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

    private OrderProduct getDummyOrderProduct(){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(getDummyOrder());
        orderProduct.setProduct(getDummyProduct());
        orderProduct.setOrderProductId(new OrderProductId(getDummyOrder().getOrderId(), getDummyProduct().getProductId()));
        orderProduct.setQuantity(0);
        orderProduct.setPrice(BigDecimal.valueOf(0));
        return orderProduct;
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
}
