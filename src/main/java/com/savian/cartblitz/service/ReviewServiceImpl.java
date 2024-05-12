package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.ReviewNotFoundException;
import com.savian.cartblitz.mapper.ReviewMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.model.Review;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }
    
    @Override
    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll().stream().map(reviewMapper::reviewToReviewDto).toList();
    }

    @Override
    public Optional<ReviewDto> getReviewById(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isPresent()) {
            return review.map(reviewMapper::reviewToReviewDto);
        }
        else {
            throw new ReviewNotFoundException(reviewId);
        }
    }

    @Override
    @Transactional
    public List<ReviewDto> getReviewsByCustomerId(Long customerId) {
        /*
        customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));

        return reviewRepository.findByCustomerCustomerId(customerId).stream().map(reviewMapper::reviewToReviewDto).toList();
        */
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            List<Review> reviews = customer.getReviews();
            return reviews.stream().map(reviewMapper::reviewToReviewDto).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<ReviewDto> getReviewsByProductId(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        return reviewRepository.findByProductProductId(productId).stream().map(reviewMapper::reviewToReviewDto).toList();
    }

    @Override
    public List<ReviewDto> getReviewsByRating(Integer rating) {
        return reviewRepository.findByRating(rating).stream().map(reviewMapper::reviewToReviewDto).toList();
    }

    @Override
    public ReviewDto saveReview(ReviewDto reviewDto) {
        customerRepository.findById(reviewDto.getCustomerId()).orElseThrow(() -> new CustomerNotFoundException(reviewDto.getCustomerId()));

        productRepository.findById(reviewDto.getProductId()).orElseThrow(() -> new ProductNotFoundException(reviewDto.getProductId()));

        reviewDto.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));

        Review savedReview = reviewRepository.save(reviewMapper.reviewDtoToReview(reviewDto));
        return reviewMapper.reviewToReviewDto(savedReview);
    }

    @Override
    public ReviewDto updateReview(Long reviewId, ReviewDto reviewDto) {
        Optional<Review> optReview = reviewRepository.findById(reviewId);
        if (optReview.isPresent()){
            Customer customer = customerRepository.findById(reviewDto.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFoundException(reviewDto.getCustomerId()));

            Product product = productRepository.findById(reviewDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(reviewDto.getProductId()));

            Review prevReview = optReview.get();

            prevReview.setProduct(product);
            prevReview.setCustomer(customer);
            prevReview.setRating(reviewDto.getRating());
            prevReview.setComment(reviewDto.getComment());
            prevReview.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));

            Review savedReview = reviewRepository.save(prevReview);
            return reviewMapper.reviewToReviewDto(savedReview);
        }
        else{
            throw new ReviewNotFoundException(reviewId);
        }
    }

    @Override
    public void removeReviewById(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if(review.isPresent()){
            reviewRepository.deleteById(reviewId);
        }
        else{
            throw new ReviewNotFoundException(reviewId);
        }
    }
}
