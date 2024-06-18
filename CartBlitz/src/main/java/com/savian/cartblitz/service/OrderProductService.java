package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.model.OrderProduct;

import java.util.List;
import java.util.Optional;

public interface OrderProductService {
    List<OrderProduct> getAllOrderProducts();
    Optional<OrderProduct> getOrderProductByOrderIdAndProductId(Long orderId, Long productId);

    List<OrderProduct> getOrderProductsByOrderId(Long orderId);
    List<OrderProduct> getOrderProductsByProductId(Long productId);

    OrderProduct saveOrderProduct(OrderProductDto orderProductDto);
    OrderProduct updateOrderProduct(Long orderId, Long productId, OrderProductDto orderProductDto);
    void removeOrderProductById(Long orderId, Long productId);
}
