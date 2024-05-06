package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.exception.CustomerNotFoundException;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.ReviewNotFoundException;
import com.savian.cartblitz.mapper.ReviewMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.model.Review;
import com.savian.cartblitz.repository.CustomerRepository;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
public class ReviewServiceUnitTest {
    @InjectMocks
    private ReviewServiceImpl reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    @Test
    public void testGetAllReviews() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(getDummyReview());

        log.info("Starting testGetAllReviews");

        Mockito.when(reviewRepository.findAll()).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getAllReviews();
        reviews.forEach(review -> log.info(String.valueOf(review.getReviewId())));

        Mockito.verify(reviewRepository).findAll();
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);

        log.info("Finished testGetAllReviews successfully");
    }

    @Test
    public void testGetReviewByIdFound() {
        Review review = getDummyReview();
        ReviewDto reviewDto = getDummyReviewDto();

        log.info("Starting testGetReviewByIdFound");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
        Mockito.when(reviewMapper.reviewToReviewDto(Mockito.any(Review.class))).thenReturn(reviewDto);

        Optional<ReviewDto> result = reviewService.getReviewById(review.getReviewId());
        result.ifPresent(value -> log.info(String.valueOf(value.getReviewId())));

        Mockito.verify(reviewRepository).findById(review.getReviewId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(reviewDto, result.get());

        log.info("Finished testGetReviewByIdFound successfully");
    }

    @Test
    public void testGetReviewByIdNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();

        log.info("Starting testGetReviewByIdNotFound");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewDto.getReviewId()));
        log.error("Review with given ID was not found");

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());

        log.info("Finished testGetReviewByIdNotFound successfully");
    }

    @Test
    public void testGetReviewsByCustomerIdFound() {
        List<Review> reviews = new ArrayList<>();
        Review review = getDummyReview();
        reviews.add(review);

        log.info("Starting testGetReviewsByCustomerIdFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(reviewRepository.findByCustomerCustomerId(Mockito.anyLong())).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getReviewsByCustomerId(review.getCustomer().getCustomerId());
        reviews.forEach(review1 -> log.info(String.valueOf(review1.getReviewId())));

        Mockito.verify(reviewRepository).findByCustomerCustomerId(review.getCustomer().getCustomerId());
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);

        log.info("Finished testGetReviewsByCustomerIdFound successfully");
    }

    @Test
    public void testGetReviewsByCustomerIdNotFound() {
        Review review = getDummyReview();

        log.info("Starting testGetReviewsByCustomerIdNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> reviewService.getReviewsByCustomerId(review.getCustomer().getCustomerId()));
        log.error("Customer with given ID was not found");

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());

        log.info("Finished testGetReviewsByCustomerIdNotFound successfully");
    }

    @Test
    public void testGetReviewsByProductIdFound() {
        List<Review> reviews = new ArrayList<>();
        Review review = getDummyReview();
        reviews.add(review);

        log.info("Starting testGetReviewsByProductIdFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getProduct()));
        Mockito.when(reviewRepository.findByProductProductId(Mockito.anyLong())).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getReviewsByProductId(review.getProduct().getProductId());
        reviews.forEach(review1 -> log.info(String.valueOf(review1.getReviewId())));

        Mockito.verify(reviewRepository).findByProductProductId(review.getProduct().getProductId());
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);

        log.info("Finished testGetReviewsByProductIdFound successfully");
    }

    @Test
    public void testGetReviewsByProductIdNotFound() {
        Review review = getDummyReview();

        log.info("Starting testGetReviewsByProductIdNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> reviewService.getReviewsByProductId(review.getProduct().getProductId()));
        log.error("Product with given ID was not found");

        Mockito.verify(productRepository).findById(review.getProduct().getProductId());

        log.info("Finished testGetReviewsByProductIdNotFound successfully");
    }

    @Test
    public void testGetReviewsByRating() {
        List<Review> reviews = new ArrayList<>();
        Review review = getDummyReview();
        reviews.add(review);

        log.info("Starting testGetReviewsByRating");

        Mockito.when(reviewRepository.findByRating(Mockito.anyInt())).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getReviewsByRating(review.getRating());
        reviews.forEach(review1 -> log.info(String.valueOf(review1.getReviewId())));

        Mockito.verify(reviewRepository).findByRating(review.getRating());
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);

        log.info("Finished testGetReviewsByRating successfully");
    }

    @Test
    public void testSaveReviewSuccess() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        log.info("Starting testSaveReviewSuccess");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getProduct()));

        Mockito.when(reviewMapper.reviewDtoToReview(reviewDto)).thenReturn(review);
        Mockito.when(reviewRepository.save(review)).thenReturn(review);
        Mockito.when(reviewMapper.reviewToReviewDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.saveReview(reviewDto);
        log.info(String.valueOf(result.getReviewId()));

        Assertions.assertEquals(reviewDto, result);

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(review.getProduct().getProductId());

        Mockito.verify(reviewMapper).reviewDtoToReview(reviewDto);
        Mockito.verify(reviewRepository).save(review);
        Mockito.verify(reviewMapper).reviewToReviewDto(review);

        log.info("Finished testSaveReviewSuccess successfully");
    }

    @Test
    public void testSaveReviewCustomerNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        log.info("Starting testSaveReviewCustomerNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> reviewService.saveReview(reviewDto));
        log.error("Customer with given ID was not found");

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());

        log.info("Finished testSaveReviewCustomerNotFound successfully");
    }

    @Test
    public void testSaveReviewProductNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        log.info("Starting testSaveReviewProductNotFound");

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> reviewService.saveReview(reviewDto));
        log.error("Product with given ID was not found");

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(review.getProduct().getProductId());

        log.info("Finished testSaveReviewProductNotFound successfully");
    }

    @Test
    public void testUpdateReviewSuccess() {
        Review existingReview = getDummyReview();
        ReviewDto reviewDto = getDummyReviewDto();

        log.info("Starting testUpdateReviewSuccess");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingReview));

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingReview.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingReview.getProduct()));
        Mockito.when(reviewRepository.save(existingReview)).thenReturn(existingReview);
        Mockito.when(reviewMapper.reviewToReviewDto(existingReview)).thenReturn(reviewDto);

        ReviewDto result = reviewService.updateReview(existingReview.getReviewId(), reviewDto);
        log.info(String.valueOf(result.getReviewId()));

        Assertions.assertEquals(reviewDto, result);

        Mockito.verify(reviewRepository).findById(existingReview.getReviewId());

        Mockito.verify(customerRepository).findById(existingReview.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(existingReview.getProduct().getProductId());
        Mockito.verify(reviewRepository).save(existingReview);
        Mockito.verify(reviewMapper).reviewToReviewDto(existingReview);

        log.info("Finished testUpdateReviewSuccess successfully");
    }

    @Test
    public void testUpdateReviewNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();

        log.info("Starting testUpdateReviewNotFound");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(reviewDto.getReviewId(), reviewDto));
        log.error("Review with given ID was not found");

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());

        log.info("Finished testUpdateReviewNotFound successfully");
    }

    @Test
    public void testUpdateReviewCustomerNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        log.info("Starting testUpdateReviewCustomerNotFound");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> reviewService.updateReview(reviewDto.getReviewId(), reviewDto));
        log.error("Customer with given ID was not found");

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());
        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());

        log.info("Finished testUpdateReviewCustomerNotFound successfully");
    }

    @Test
    public void testUpdateReviewProductNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        log.info("Starting testUpdateReviewProductNotFound");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> reviewService.updateReview(reviewDto.getReviewId(), reviewDto));
        log.error("Product with given ID was not found");

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());
        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(review.getProduct().getProductId());

        log.info("Finished testUpdateReviewProductNotFound successfully");
    }

    @Test
    public void testRemoveReviewByIdSuccess() {
        Review review = getDummyReview();

        log.info("Starting testRemoveReviewByIdSuccess");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));

        reviewService.removeReviewById(review.getReviewId());
        log.info(String.valueOf(review.getReviewId()));

        Mockito.verify(reviewRepository).findById(review.getReviewId());
        Mockito.verify(reviewRepository).deleteById(review.getReviewId());

        log.info("Finished testRemoveReviewByIdSuccess successfully");
    }

    @Test
    public void testRemoveReviewByIdNotFound() {
        Review review = getDummyReview();

        log.info("Starting testRemoveReviewByIdNotFound");

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ReviewNotFoundException.class, () -> reviewService.removeReviewById(review.getReviewId()));
        log.error("Review with given ID was not found");

        Mockito.verify(reviewRepository).findById(review.getReviewId());

        log.info("Finished testRemoveReviewByIdNotFound successfully");
    }

    private Review getDummyReview(){
        Review review = new Review();
        review.setReviewId(10L);
        review.setCustomer(getDummyCustomer());
        review.setProduct(getDummyProduct());
        review.setRating(5);
        review.setComment("Review Test Comment");
        review.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));
        return review;
    }

    private ReviewDto getDummyReviewDto(){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(10L);
        reviewDto.setCustomerId(getDummyCustomer().getCustomerId());
        reviewDto.setProductId(getDummyProduct().getProductId());
        reviewDto.setRating(5);
        reviewDto.setComment("Review Test Comment");
        reviewDto.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));
        return reviewDto;
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
}
