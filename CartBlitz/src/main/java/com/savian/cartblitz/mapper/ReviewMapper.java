package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.model.Review;
import com.savian.cartblitz.repository.*;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReviewMapper(CustomerRepository customerRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public ReviewDto reviewToReviewDto(Review review){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(review.getReviewId());
        reviewDto.setCustomerId(review.getCustomer().getCustomerId());
        reviewDto.setProductId(review.getProduct().getProductId());
        reviewDto.setRating(review.getRating());
        reviewDto.setComment(review.getComment());
        reviewDto.setReviewDate(review.getReviewDate());
        return reviewDto;
    }

    public Review reviewDtoToReview(ReviewDto reviewDto){
        Review review = new Review();
        review.setReviewId(reviewDto.getReviewId());
        review.setCustomer(customerRepository.getReferenceById(reviewDto.getCustomerId()));
        review.setProduct(productRepository.getReferenceById(reviewDto.getProductId()));
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setReviewDate(reviewDto.getReviewDate());
        return review;
    }
}
