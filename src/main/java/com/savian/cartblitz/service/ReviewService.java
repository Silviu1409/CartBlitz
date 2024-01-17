package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ReviewDto;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewDto> getAllReviews();
    Optional<ReviewDto> getReviewById(Long reviewId);

    List<ReviewDto> getReviewsByCustomerId(Long customerId);
    List<ReviewDto> getReviewsByProductId(Long productId);
    List<ReviewDto> getReviewsByRating(Integer rating);

    ReviewDto saveReview(ReviewDto reviewDto);
    ReviewDto updateReview(Long reviewId, ReviewDto reviewDto);
    void removeReviewById(Long reviewId);
}
