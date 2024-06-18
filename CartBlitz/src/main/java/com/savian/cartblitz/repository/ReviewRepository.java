package com.savian.cartblitz.repository;

import com.savian.cartblitz.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCustomerCustomerId(Long customerId);
    List<Review> findByProductProductId(Long productId);
    List<Review> findByRating(Integer rating);
}
