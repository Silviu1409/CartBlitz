package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.model.OrderProduct;
import com.savian.cartblitz.model.OrderProductId;
import com.savian.cartblitz.repository.OrderRepository;
import com.savian.cartblitz.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderProductMapper {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderProductMapper(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }
    public OrderProductDto orderProductToOrderProductDto (OrderProduct orderProduct){
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setOrderId(orderProduct.getOrder().getOrderId());
        orderProductDto.setProductId(orderProduct.getProduct().getProductId());
        orderProductDto.setQuantity(orderProduct.getQuantity());
        orderProductDto.setPrice(orderProduct.getPrice());
        return orderProductDto;
    }

    public OrderProduct orderProductDtoToOrderProduct (OrderProductDto orderProductDto){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(new OrderProductId(orderProductDto.getOrderId(), orderProductDto.getProductId()));
        orderProduct.setOrder(orderRepository.getReferenceById(orderProductDto.getOrderId()));
        orderProduct.setProduct(productRepository.getReferenceById(orderProductDto.getProductId()));
        orderProduct.setQuantity(orderProduct.getQuantity());
        orderProduct.setPrice(orderProductDto.getPrice());
        return orderProduct;
    }
}
