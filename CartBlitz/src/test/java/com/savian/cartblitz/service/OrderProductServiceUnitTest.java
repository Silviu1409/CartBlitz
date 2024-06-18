package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.exception.*;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
@org.junit.jupiter.api.Tag("test")
public class OrderProductServiceUnitTest {
    @InjectMocks
    private OrderProductServiceImpl orderProductService;
    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;

    @Test
    public void testGetAllOrderProducts() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(getDummyOrderProduct());

        log.info("Starting testGetAllOrderProducts");
        
        Mockito.when(orderProductRepository.findAll()).thenReturn(orderProducts);

        List<OrderProduct> result = orderProductService.getAllOrderProducts();
        orderProducts.forEach(orderProduct -> log.info(String.valueOf(orderProduct.getOrderProductId())));

        Assertions.assertEquals(orderProducts, result);

        Mockito.verify(orderProductRepository).findAll();

        log.info("Finished testGetAllCustomers successfully");
    }

    @Test
    public void testGetOrderProductByIdFound() {
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testGetOrderProductByIdFound");

        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(orderProduct));

        Optional<OrderProduct> result = orderProductService.getOrderProductByOrderIdAndProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());
        result.ifPresent(value -> log.info(String.valueOf(value.getOrderProductId())));

        Mockito.verify(orderProductRepository).findByOrderOrderIdAndProductProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());

        log.info("Finished testGetOrderProductByIdFound successfully");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(orderProduct, result.get());
    }

    @Test
    public void testGetOrderProductByIdNotFound() {
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testGetOrderProductByIdNotFound");

        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderProductNotFoundException.class, () -> orderProductService.getOrderProductByOrderIdAndProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId()));
        log.error("OrderProduct with given ID was not found");

        Mockito.verify(orderProductRepository).findByOrderOrderIdAndProductProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());

        log.info("Finished testGetOrderProductByIdNotFound successfully");
    }

    @Test
    public void testGetOrderProductsByOrderIdFound() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        OrderProduct orderProduct = getDummyOrderProduct();
        orderProducts.add(orderProduct);

        log.info("Starting testGetOrderProductsByOrderIdFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(orderProduct.getOrder()));
        Mockito.when(orderProductRepository.findByOrderOrderId(Mockito.anyLong())).thenReturn(orderProducts);

        List<OrderProduct> result = orderProductService.getOrderProductsByOrderId(orderProduct.getOrder().getOrderId());
        orderProducts.forEach(orderProduct1 -> log.info(String.valueOf(orderProduct1.getOrderProductId())));

        Mockito.verify(orderProductRepository).findByOrderOrderId(orderProduct.getOrder().getOrderId());

        log.info("Finished testGetOrderProductsByOrderIdFound successfully");

        Assertions.assertEquals(orderProducts, result);
    }

    @Test
    public void testGetOrderProductsByOrderIdNotFound() {
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testGetOrderProductsByOrderIdNotFound");

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderProductService.getOrderProductsByOrderId(orderProduct.getOrder().getOrderId()));
        log.error("OrderProduct with given orderId was not found");

        Mockito.verify(orderRepository).findById(orderProduct.getOrder().getOrderId());

        log.info("Finished testGetOrderProductsByOrderIdNotFound successfully");
    }

    @Test
    public void testGetOrderProductsByProductIdFound() {
        List<OrderProduct> orderProducts = new ArrayList<>();
        OrderProduct orderProduct = getDummyOrderProduct();
        orderProducts.add(orderProduct);

        log.info("Starting testGetOrderProductsByProductIdFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(orderProduct.getProduct()));
        Mockito.when(orderProductRepository.findByProductProductId(Mockito.anyLong())).thenReturn(orderProducts);

        List<OrderProduct> result = orderProductService.getOrderProductsByProductId(orderProduct.getProduct().getProductId());
        orderProducts.forEach(orderProduct1 -> log.info(String.valueOf(orderProduct1.getOrderProductId())));

        Mockito.verify(orderProductRepository).findByProductProductId(orderProduct.getProduct().getProductId());

        log.info("Finished testGetOrderProductsByProductIdFound successfully");

        Assertions.assertEquals(orderProducts, result);
    }

    @Test
    public void testGetOrderProductsByProductIdNotFound() {
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testGetOrderProductsByProductIdNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> orderProductService.getOrderProductsByProductId(orderProduct.getProduct().getProductId()));
        log.error("OrderProduct with given productId was not found");

        Mockito.verify(productRepository).findById(orderProduct.getProduct().getProductId());

        log.info("Finished testGetOrderProductsByProductIdNotFound successfully");
    }

    @Test
    public void testSaveOrderProduct() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testSaveOrderProduct");

        Mockito.when(orderRepository.findById(orderProductDto.getOrderId())).thenReturn(Optional.of(orderProduct.getOrder()));
        Mockito.when(productRepository.findById(orderProductDto.getProductId())).thenReturn(Optional.of(orderProduct.getProduct()));
        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(orderProductRepository.save(Mockito.any(OrderProduct.class))).thenReturn(orderProduct);

        OrderProduct result = orderProductService.saveOrderProduct(orderProductDto);
        log.info(String.valueOf(result.getOrderProductId()));

        log.info("Finished testSaveOrderProduct successfully");

        Assertions.assertEquals(orderProduct, result);
    }

    @Test
    public void testSaveOrderProductOrderNotFound() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();

        log.info("Starting testSaveOrderProductOrderNotFound");

        Mockito.when(orderRepository.findById(orderProductDto.getOrderId())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderProductService.saveOrderProduct(orderProductDto));
        log.error("OrderProduct with given orderId was not found");

        log.info("Finished testSaveOrderProductOrderNotFound successfully");
    }

    @Test
    public void testSaveOrderProductProductNotFound() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testSaveOrderProductProductNotFound");

        Mockito.when(orderRepository.findById(orderProductDto.getOrderId())).thenReturn(Optional.of(orderProduct.getOrder()));
        Mockito.when(productRepository.findById(orderProductDto.getProductId())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> orderProductService.saveOrderProduct(orderProductDto));
        log.error("OrderProduct with given orderId was not found");

        log.info("Finished testSaveOrderProductProductNotFound successfully");
    }

    @Test
    public void testSaveOrderProductUpdateOrderProduct() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();
        OrderProduct existingOrderProduct = getDummyOrderProduct();

        log.info("Starting testSaveOrderProductUpdateOrderProduct");

        Mockito.when(orderRepository.findById(orderProductDto.getOrderId())).thenReturn(Optional.of(existingOrderProduct.getOrder()));
        Mockito.when(productRepository.findById(orderProductDto.getProductId())).thenReturn(Optional.of(existingOrderProduct.getProduct()));
        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(existingOrderProduct));
        Mockito.when(orderProductRepository.save(Mockito.any(OrderProduct.class))).thenReturn(existingOrderProduct);

        OrderProduct result = orderProductService.saveOrderProduct(orderProductDto);
        log.info(String.valueOf(result.getOrderProductId()));

        Assertions.assertEquals(existingOrderProduct, result);

        Mockito.verify(orderService, Mockito.times(2)).modifyTotalAmount(Mockito.anyLong(), Mockito.any(BigDecimal.class));

        log.info("Finished testSaveOrderProductUpdateOrderProduct successfully");
    }

    @Test
    public void testSaveOrderProductProductQuantityException() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();
        OrderProduct orderProduct = getDummyOrderProduct();
        orderProductDto.setQuantity(1);

        log.info("Starting testSaveOrderProductProductQuantityException");

        Mockito.when(orderRepository.findById(orderProductDto.getOrderId())).thenReturn(Optional.of(orderProduct.getOrder()));
        Mockito.when(productRepository.findById(orderProductDto.getProductId())).thenReturn(Optional.of(orderProduct.getProduct()));
        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductQuantityException.class, () -> orderProductService.saveOrderProduct(orderProductDto));
        log.error("There are not enough products in the db");

        log.info("Finished testSaveOrderProductProductQuantityException successfully");
    }

    @Test
    public void testUpdateOrderProduct() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();
        OrderProduct existingOrderProduct = getDummyOrderProduct();

        log.info("Starting testUpdateOrderProduct");

        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(orderProductDto.getOrderId(), orderProductDto.getProductId())).thenReturn(Optional.of(existingOrderProduct));
        Mockito.when(orderRepository.findById(orderProductDto.getOrderId())).thenReturn(Optional.of(existingOrderProduct.getOrder()));
        Mockito.when(productRepository.findById(orderProductDto.getProductId())).thenReturn(Optional.of(existingOrderProduct.getProduct()));
        Mockito.when(orderProductRepository.save(Mockito.any(OrderProduct.class))).thenReturn(existingOrderProduct);

        OrderProduct result = orderProductService.updateOrderProduct(orderProductDto.getOrderId(), orderProductDto.getProductId(), orderProductDto);
        log.info(String.valueOf(result.getOrderProductId()));

        Assertions.assertEquals(existingOrderProduct, result);
        Mockito.verify(orderService, Mockito.times(2)).modifyTotalAmount(Mockito.anyLong(), Mockito.any(BigDecimal.class));

        log.info("Finished testUpdateOrderProduct successfully");
    }

    @Test
    public void testUpdateOrderProductOrderProductNotFound() {
        OrderProductDto orderProductDto = getDummyOrderProductDto();

        log.info("Starting testUpdateOrderProductOrderProductNotFound");

        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(orderProductDto.getOrderId(), orderProductDto.getProductId())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderProductNotFoundException.class, () -> orderProductService.updateOrderProduct(orderProductDto.getOrderId(), orderProductDto.getProductId(), orderProductDto));
        log.error("OrderProduct with given ID was not found");

        log.info("Finished testUpdateOrderProductOrderProductNotFound successfully");
    }

    @Test
    public void testRemoveOrderProductByIdSuccess() {
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testRemoveOrderProductByIdSuccess");

        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId())).thenReturn(Optional.of(orderProduct));

        orderProductService.removeOrderProductById(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());
        log.info(String.valueOf(orderProduct.getOrderProductId()));

        Mockito.verify(orderService).modifyTotalAmount(Mockito.anyLong(), Mockito.any(BigDecimal.class));
        Mockito.verify(orderProductRepository).deleteByOrderIdAndProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());

        log.info("Finished testRemoveOrderProductByIdSuccess successfully");
    }

    @Test
    public void testRemoveOrderProductByIdNotFound() {
        OrderProduct orderProduct = getDummyOrderProduct();

        log.info("Starting testRemoveOrderProductByIdNotFound");

        Mockito.when(orderProductRepository.findByOrderOrderIdAndProductProductId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderProductNotFoundException.class, () -> orderProductService.removeOrderProductById(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId()));
        log.error("OrderProduct with given ID was not found");

        Mockito.verify(orderProductRepository).findByOrderOrderIdAndProductProductId(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId());

        log.info("Finished testRemoveOrderProductByIdNotFound successfully");
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

    private OrderProductDto getDummyOrderProductDto(){
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setOrderId(getDummyOrder().getOrderId());
        orderProductDto.setProductId(getDummyProduct().getProductId());
        orderProductDto.setQuantity(0);
        orderProductDto.setPrice(BigDecimal.valueOf(0));
        return orderProductDto;
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
