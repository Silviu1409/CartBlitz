package com.savian.cartblitz.repository;

import com.savian.cartblitz.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    List<Warranty> findByOrderOrderId(Long orderId);
    List<Warranty> findByProductProductId(Long productId);
}
