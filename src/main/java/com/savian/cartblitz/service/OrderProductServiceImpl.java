package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.exception.*;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderProductServiceImpl implements OrderProductService{
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderProductServiceImpl(OrderProductRepository orderProductRepository, OrderRepository orderRepository, OrderService orderService, ProductRepository productRepository, ProductService productService) {
        this.orderProductRepository = orderProductRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.productService = productService;
    }
    
    @Override
    public List<OrderProduct> getAllOrderProducts() {
        return orderProductRepository.findAll();
    }

    @Override
    public Optional<OrderProduct> getOrderProductByOrderIdAndProductId(Long orderId, Long productId) {
        Optional<OrderProduct> orderProduct = orderProductRepository.findByOrderOrderIdAndProductProductId(orderId, productId);
        if (orderProduct.isPresent()) {
            return orderProduct;
        }
        else {
            throw new OrderProductNotFoundException(orderId, productId);
        }
    }

    @Override
    public List<OrderProduct> getOrderProductsByOrderId(Long orderId) {
        orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        return orderProductRepository.findByOrderOrderId(orderId);
    }

    @Override
    public List<OrderProduct> getOrderProductsByProductId(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        return orderProductRepository.findByProductProductId(productId);
    }

    @Override
    public OrderProduct saveOrderProduct(OrderProductDto orderProductDto) {
        Order order = orderRepository.findById(orderProductDto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(orderProductDto.getOrderId()));

        Product product = productRepository.findById(orderProductDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(orderProductDto.getProductId()));

        if (orderProductRepository.findByOrderOrderIdAndProductProductId(order.getOrderId(), product.getProductId()).isPresent()){
            return updateOrderProduct(order.getOrderId(), product.getProductId(), orderProductDto);
        }
        else if (orderProductDto.getQuantity() > product.getStockQuantity()){
            throw new ProductQuantityException(product.getProductId(), product.getStockQuantity());
        }
        else {
            orderService.modifyTotalAmount(order.getOrderId(), BigDecimal.valueOf(orderProductDto.getQuantity()).multiply(product.getPrice()));

            OrderProduct orderProduct = new OrderProduct();

            orderProduct.setOrderProductId(new OrderProductId(order.getOrderId(), product.getProductId()));
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(orderProductDto.getQuantity());
            orderProduct.setPrice(product.getPrice());

            return orderProductRepository.save(orderProduct);
        }
    }

    @Override
    public OrderProduct updateOrderProduct(Long orderId, Long productId, OrderProductDto orderProductDto) {
        Optional<OrderProduct> optOrderProduct = orderProductRepository.findByOrderOrderIdAndProductProductId(orderId, productId);
        if (optOrderProduct.isPresent()){
            OrderProduct prevOrderProduct = optOrderProduct.get();

            Order order = orderRepository.findById(orderProductDto.getOrderId())
                    .orElseThrow(() -> new OrderNotFoundException(orderProductDto.getOrderId()));

            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(orderProductDto.getProductId()));

            if (orderProductDto.getQuantity() > product.getStockQuantity()){
                throw new ProductQuantityException(product.getProductId(), product.getStockQuantity());
            }
            else {
                orderService.modifyTotalAmount(prevOrderProduct.getOrder().getOrderId(), BigDecimal.valueOf(prevOrderProduct.getQuantity()).multiply(prevOrderProduct.getPrice()).negate());

                prevOrderProduct.setOrderProductId(new OrderProductId(order.getOrderId(), product.getProductId()));
                prevOrderProduct.setOrder(order);
                prevOrderProduct.setProduct(product);
                prevOrderProduct.setQuantity(orderProductDto.getQuantity());
                prevOrderProduct.setPrice(BigDecimal.valueOf(orderProductDto.getQuantity()).multiply(product.getPrice()));

                orderService.modifyTotalAmount(order.getOrderId(), BigDecimal.valueOf(orderProductDto.getQuantity()).multiply(product.getPrice()));

                return orderProductRepository.save(prevOrderProduct);
            }
        }
        else{
            throw new OrderProductNotFoundException(orderId, productId);
        }
    }

    @Transactional
    @Override
    public void removeOrderProductById(Long orderId, Long productId) {
        Optional<OrderProduct> optOrderProduct = orderProductRepository.findByOrderOrderIdAndProductProductId(orderId, productId);
        if(optOrderProduct.isPresent()){
            OrderProduct orderProduct = optOrderProduct.get();

            orderService.modifyTotalAmount(orderProduct.getOrder().getOrderId(), BigDecimal.valueOf(orderProduct.getQuantity()).multiply(orderProduct.getPrice()).negate());

            orderProductRepository.deleteByOrderIdAndProductId(orderId, productId);
        }
        else{
            throw new OrderProductNotFoundException(orderId, productId);
        }
    }
}
