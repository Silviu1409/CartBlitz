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

        Mockito.when(reviewRepository.findAll()).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getAllReviews();

        Mockito.verify(reviewRepository).findAll();
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);
    }

    @Test
    public void testGetReviewByIdFound() {
        Review review = getDummyReview();
        ReviewDto reviewDto = getDummyReviewDto();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
        Mockito.when(reviewMapper.reviewToReviewDto(Mockito.any(Review.class))).thenReturn(reviewDto);

        Optional<ReviewDto> result = reviewService.getReviewById(review.getReviewId());

        Mockito.verify(reviewRepository).findById(review.getReviewId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(reviewDto, result.get());
    }

    @Test
    public void testGetReviewByIdNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ReviewNotFoundException.class, () -> reviewService.getReviewById(reviewDto.getReviewId()));

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());
    }

    @Test
    public void testGetReviewsByCustomerIdFound() {
        List<Review> reviews = new ArrayList<>();
        Review review = getDummyReview();
        reviews.add(review);

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(reviewRepository.findByCustomerCustomerId(Mockito.anyLong())).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getReviewsByCustomerId(review.getCustomer().getCustomerId());

        Mockito.verify(reviewRepository).findByCustomerCustomerId(review.getCustomer().getCustomerId());
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);
    }

    @Test
    public void testGetReviewsByCustomerIdNotFound() {
        Review review = getDummyReview();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> reviewService.getReviewsByCustomerId(review.getCustomer().getCustomerId()));

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
    }

    @Test
    public void testGetReviewsByProductIdFound() {
        List<Review> reviews = new ArrayList<>();
        Review review = getDummyReview();
        reviews.add(review);

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getProduct()));
        Mockito.when(reviewRepository.findByProductProductId(Mockito.anyLong())).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getReviewsByProductId(review.getProduct().getProductId());

        Mockito.verify(reviewRepository).findByProductProductId(review.getProduct().getProductId());
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);
    }

    @Test
    public void testGetReviewsByProductIdNotFound() {
        Review review = getDummyReview();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> reviewService.getReviewsByProductId(review.getProduct().getProductId()));

        Mockito.verify(productRepository).findById(review.getProduct().getProductId());
    }

    @Test
    public void testGetReviewsByRating() {
        List<Review> reviews = new ArrayList<>();
        Review review = getDummyReview();
        reviews.add(review);

        Mockito.when(reviewRepository.findByRating(Mockito.anyInt())).thenReturn(reviews);

        List<ReviewDto> result = reviewService.getReviewsByRating(review.getRating());

        Mockito.verify(reviewRepository).findByRating(review.getRating());
        Assertions.assertEquals(reviews.stream().map(reviewMapper::reviewToReviewDto).toList(), result);
    }

    @Test
    public void testSaveReviewSuccess() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getProduct()));

        Mockito.when(reviewMapper.reviewDtoToReview(reviewDto)).thenReturn(review);
        Mockito.when(reviewRepository.save(review)).thenReturn(review);
        Mockito.when(reviewMapper.reviewToReviewDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.saveReview(reviewDto);
        Assertions.assertEquals(reviewDto, result);

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(review.getProduct().getProductId());

        Mockito.verify(reviewMapper).reviewDtoToReview(reviewDto);
        Mockito.verify(reviewRepository).save(review);
        Mockito.verify(reviewMapper).reviewToReviewDto(review);
    }

    @Test
    public void testSaveReviewCustomerNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> reviewService.saveReview(reviewDto));

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
    }

    @Test
    public void testSaveReviewProductNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> reviewService.saveReview(reviewDto));

        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(review.getProduct().getProductId());
    }

    @Test
    public void testUpdateReviewSuccess() {
        Review existingReview = getDummyReview();
        ReviewDto reviewDto = getDummyReviewDto();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingReview));

        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingReview.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingReview.getProduct()));
        Mockito.when(reviewRepository.save(existingReview)).thenReturn(existingReview);
        Mockito.when(reviewMapper.reviewToReviewDto(existingReview)).thenReturn(reviewDto);

        ReviewDto result = reviewService.updateReview(existingReview.getReviewId(), reviewDto);
        Assertions.assertEquals(reviewDto, result);

        Mockito.verify(reviewRepository).findById(existingReview.getReviewId());

        Mockito.verify(customerRepository).findById(existingReview.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(existingReview.getProduct().getProductId());
        Mockito.verify(reviewRepository).save(existingReview);
        Mockito.verify(reviewMapper).reviewToReviewDto(existingReview);
    }

    @Test
    public void testUpdateReviewNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ReviewNotFoundException.class, () -> reviewService.updateReview(reviewDto.getReviewId(), reviewDto));

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());
    }

    @Test
    public void testUpdateReviewCustomerNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomerNotFoundException.class, () -> reviewService.updateReview(reviewDto.getReviewId(), reviewDto));

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());
        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
    }

    @Test
    public void testUpdateReviewProductNotFound() {
        ReviewDto reviewDto = getDummyReviewDto();
        Review review = getDummyReview();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));
        Mockito.when(customerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review.getCustomer()));
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> reviewService.updateReview(reviewDto.getReviewId(), reviewDto));

        Mockito.verify(reviewRepository).findById(reviewDto.getReviewId());
        Mockito.verify(customerRepository).findById(review.getCustomer().getCustomerId());
        Mockito.verify(productRepository).findById(review.getProduct().getProductId());
    }

    @Test
    public void testRemoveReviewByIdSuccess() {
        Review review = getDummyReview();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(review));

        reviewService.removeReviewById(review.getReviewId());

        Mockito.verify(reviewRepository).findById(review.getReviewId());
        Mockito.verify(reviewRepository).deleteById(review.getReviewId());
    }

    @Test
    public void testRemoveReviewByIdNotFound() {
        Review review = getDummyReview();

        Mockito.when(reviewRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ReviewNotFoundException.class, () -> reviewService.removeReviewById(review.getReviewId()));

        Mockito.verify(reviewRepository).findById(review.getReviewId());
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
