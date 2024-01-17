package com.savian.cartblitz.repository;

import com.savian.cartblitz.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderOrderId(Long orderId);
    List<OrderProduct> findByProductProductId(Long productId);
    Optional<OrderProduct> findByOrderOrderIdAndProductProductId(Long orderId, Long productId);

    @Modifying
    @Query("DELETE FROM OrderProduct op WHERE op.order.orderId = :orderId AND op.product.productId = :productId")
    void deleteByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
}
