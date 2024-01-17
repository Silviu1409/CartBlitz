package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.OrderNotFoundException;
import com.savian.cartblitz.exception.OrderProductNotFoundException;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.WarrantyNotFoundException;
import com.savian.cartblitz.mapper.WarrantyMapper;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.WarrantyRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class WarrantyServiceUnitTest {
    @InjectMocks
    private WarrantyServiceImpl warrantyService;
    @Mock
    private WarrantyRepository warrantyRepository;
    @Mock
    private WarrantyMapper warrantyMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderProductService orderProductService;

    @Test
    public void testGetAllWarranties() {
        List<Warranty> warranties = new ArrayList<>();
        warranties.add(getDummyWarranty());

        Mockito.when(warrantyRepository.findAll()).thenReturn(warranties);

        List<WarrantyDto> result = warrantyService.getAllWarranties();

        Mockito.verify(warrantyRepository).findAll();
        Assertions.assertEquals(warranties.stream().map(warrantyMapper::warrantyToWarrantyDto).toList(), result);
    }

    @Test
    public void testGetWarrantyByIdFound() {
        Warranty warranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any(Warranty.class))).thenReturn(warrantyDto);

        Optional<WarrantyDto> result = warrantyService.getWarrantyById(warranty.getWarrantyId());

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(warrantyDto, result.get());
    }

    @Test
    public void testGetWarrantyByIdNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.getWarrantyById(warrantyDto.getWarrantyId()));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
    }

    @Test
    public void testGetWarrantiesByOrderIdFound() {
        List<Warranty> warranties = new ArrayList<>();
        Warranty warranty = getDummyWarranty();
        warranties.add(warranty);

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getOrder()));
        Mockito.when(warrantyRepository.findByOrderOrderId(Mockito.anyLong())).thenReturn(warranties);

        List<WarrantyDto> result = warrantyService.getWarrantiesByOrderId(warranty.getOrder().getOrderId());

        Mockito.verify(warrantyRepository).findByOrderOrderId(warranty.getOrder().getOrderId());
        Assertions.assertEquals(warranties.stream().map(warrantyMapper::warrantyToWarrantyDto).toList(), result);
    }

    @Test
    public void testGetWarrantiesByOrderIdNotFound() {
        Warranty warranty = getDummyWarranty();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> warrantyService.getWarrantiesByOrderId(warranty.getOrder().getOrderId()));

        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
    }

    @Test
    public void testGetWarrantiesByProductIdFound() {
        List<Warranty> warranties = new ArrayList<>();
        Warranty warranty = getDummyWarranty();
        warranties.add(warranty);

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getProduct()));
        Mockito.when(warrantyRepository.findByProductProductId(Mockito.anyLong())).thenReturn(warranties);

        List<WarrantyDto> result = warrantyService.getWarrantiesByProductId(warranty.getProduct().getProductId());

        Mockito.verify(warrantyRepository).findByProductProductId(warranty.getProduct().getProductId());
        Assertions.assertEquals(warranties.stream().map(warrantyMapper::warrantyToWarrantyDto).toList(), result);
    }

    @Test
    public void testGetWarrantiesByProductIdNotFound() {
        Warranty warranty = getDummyWarranty();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> warrantyService.getWarrantiesByProductId(warranty.getProduct().getProductId()));

        Mockito.verify(productRepository).findById(warranty.getProduct().getProductId());
    }

    @Test
    void testSaveWarrantySuccess() {
        Order order = getDummyOrder();
        Product product = getDummyProduct();
        Warranty warranty = getDummyWarranty();
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        OrderProduct orderProduct = getDummyOrderProduct();

        Mockito.when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(orderProductService.getOrderProductByOrderIdAndProductId(Mockito.any(), Mockito.any())).thenReturn(Optional.of(orderProduct));
        Mockito.when(warrantyRepository.save(Mockito.any())).thenReturn(warranty);
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any())).thenReturn(warrantyDto);

        WarrantyDto result = warrantyService.saveWarranty(warrantyDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(warrantyDto.getWarrantyId(), result.getWarrantyId());
    }

    @Test
    public void testSaveWarrantyOrderNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        Warranty warranty = getDummyWarranty();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> warrantyService.saveWarranty(warrantyDto));

        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
    }

    @Test
    public void testSaveWarrantyProductNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        Warranty warranty = getDummyWarranty();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getOrder()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> warrantyService.saveWarranty(warrantyDto));

        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
        Mockito.verify(productRepository).findById(warranty.getProduct().getProductId());
    }

    @Test
    public void testSaveWarrantyOrderProductNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        Warranty warranty = getDummyWarranty();

        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getOrder()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getProduct()));
        Mockito.when(orderProductService.getOrderProductByOrderIdAndProductId(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderProductNotFoundException.class, () -> warrantyService.saveWarranty(warrantyDto));

        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
        Mockito.verify(productRepository).findById(warranty.getProduct().getProductId());
    }

    @Test
    void testUpdateWarrantySuccess() {
        Warranty existingWarranty = getDummyWarranty();
        Order order = getDummyOrder();
        Product product = getDummyProduct();
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        OrderProduct orderProduct = getDummyOrderProduct();

        Mockito.when(warrantyRepository.findById(Mockito.any())).thenReturn(Optional.of(existingWarranty));
        Mockito.when(orderRepository.findById(Mockito.any())).thenReturn(Optional.of(order));
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(product));
        Mockito.when(orderProductService.getOrderProductByOrderIdAndProductId(Mockito.any(), Mockito.any())).thenReturn(Optional.of(orderProduct));
        Mockito.when(warrantyRepository.save(Mockito.any())).thenReturn(existingWarranty);
        Mockito.when(warrantyMapper.warrantyToWarrantyDto(Mockito.any())).thenReturn(warrantyDto);

        WarrantyDto result = warrantyService.updateWarranty(existingWarranty.getWarrantyId(), warrantyDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(warrantyDto.getWarrantyId(), result.getWarrantyId());
    }

    @Test
    public void testUpdateWarrantyNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.updateWarranty(warrantyDto.getWarrantyId(), warrantyDto));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
    }

    @Test
    public void testUpdateWarrantyOrderNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> warrantyService.updateWarranty(warrantyDto.getWarrantyId(), warrantyDto));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
    }

    @Test
    public void testUpdateWarrantyProductNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getOrder()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> warrantyService.updateWarranty(warrantyDto.getWarrantyId(), warrantyDto));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
        Mockito.verify(productRepository).findById(warranty.getProduct().getProductId());
    }

    @Test
    public void testUpdateWarrantyOrderProductNotFound() {
        WarrantyDto warrantyDto = getDummyWarrantyDto();
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getOrder()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty.getProduct()));
        Mockito.when(orderProductService.getOrderProductByOrderIdAndProductId(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderProductNotFoundException.class, () -> warrantyService.updateWarranty(warrantyDto.getWarrantyId(), warrantyDto));

        Mockito.verify(warrantyRepository).findById(warrantyDto.getWarrantyId());
        Mockito.verify(orderRepository).findById(warranty.getOrder().getOrderId());
        Mockito.verify(productRepository).findById(warranty.getProduct().getProductId());
    }

    @Test
    public void testRemoveWarrantyByIdSuccess() {
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(warranty));

        warrantyService.removeWarrantyById(warranty.getWarrantyId());

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
        Mockito.verify(warrantyRepository).deleteById(warranty.getWarrantyId());
    }

    @Test
    public void testRemoveWarrantyByIdNotFound() {
        Warranty warranty = getDummyWarranty();

        Mockito.when(warrantyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(WarrantyNotFoundException.class, () -> warrantyService.removeWarrantyById(warranty.getWarrantyId()));

        Mockito.verify(warrantyRepository).findById(warranty.getWarrantyId());
    }

    private Warranty getDummyWarranty(){
        Warranty warranty = new Warranty();
        warranty.setWarrantyId(10L);
        warranty.setOrder(getDummyOrder());
        warranty.setProduct(getDummyProduct());
        warranty.setDurationMonths(0);
        return warranty;
    }

    private WarrantyDto getDummyWarrantyDto(){
        WarrantyDto warrantyDto = new WarrantyDto();
        warrantyDto.setWarrantyId(10L);
        warrantyDto.setOrderId(getDummyOrder().getOrderId());
        warrantyDto.setProductId(getDummyProduct().getProductId());
        warrantyDto.setDurationMonths(0);
        return warrantyDto;
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

    private Customer getDummyCustomer(){
        Customer customer = new Customer();
        customer.setCustomerId(10L);
        customer.setUsername("userTest");
        customer.setPassword("718%BYYLo");
        customer.setEmail("test@test.com");
        customer.setFullName("User Test");
        return customer;
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

    private OrderProduct getDummyOrderProduct(){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrder(getDummyOrder());
        orderProduct.setProduct(getDummyProduct());
        orderProduct.setOrderProductId(new OrderProductId(getDummyOrder().getOrderId(), getDummyProduct().getProductId()));
        orderProduct.setQuantity(0);
        orderProduct.setPrice(BigDecimal.valueOf(0));
        return orderProduct;
    }
}
