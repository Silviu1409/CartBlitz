package com.savian.cartblitz.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.exception.ReviewNotFoundException;
import com.savian.cartblitz.mapper.ReviewMapper;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mysql")
@org.junit.jupiter.api.Tag("test")
public class ReviewControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReviewService reviewService;
    @MockBean
    private ReviewMapper reviewMapper;

    @Test
    public void testGetAllReviews() throws Exception {
        List<ReviewDto> reviewDtoList = Arrays.asList(getDummyReviewDtoOne(), getDummyReviewDtoTwo());

        when(reviewService.getAllReviews()).thenReturn(reviewDtoList);

        mockMvc.perform(get("/review")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetReviewById() throws Exception {
        ReviewDto reviewDto = getDummyReviewDtoOne();

        when(reviewService.getReviewById(reviewDto.getReviewId())).thenReturn(Optional.of(reviewDto));

        mockMvc.perform(get("/review/id/{reviewId}", reviewDto.getReviewId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reviewId", is(reviewDto.getReviewId().intValue())));
    }

    @Test
    public void testGetReviewByIdNotFound() throws Exception {
        Long reviewId = 99L;

        when(reviewService.getReviewById(reviewId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/review/id/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetReviewsByCustomerId() throws Exception {
        ReviewDto reviewDtoOne = getDummyReviewDtoOne();
        ReviewDto reviewDtoTwo = getDummyReviewDtoTwo();
        List<ReviewDto> reviewDtoList = Arrays.asList(reviewDtoOne, reviewDtoTwo);

        when(reviewService.getReviewsByCustomerId(reviewDtoOne.getCustomerId())).thenReturn(reviewDtoList);

        mockMvc.perform(get("/review/customer/{customerId}", reviewDtoOne.getCustomerId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetReviewsByProductId() throws Exception {
        ReviewDto reviewDtoOne = getDummyReviewDtoOne();
        ReviewDto reviewDtoTwo = getDummyReviewDtoTwo();
        List<ReviewDto> reviewDtoList = Arrays.asList(reviewDtoOne, reviewDtoTwo);

        when(reviewService.getReviewsByProductId(reviewDtoOne.getProductId())).thenReturn(reviewDtoList);

        mockMvc.perform(get("/review/product/{productId}", reviewDtoOne.getProductId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetReviewsByRating() throws Exception {
        Integer rating = 5;
        List<ReviewDto> reviewDtoList = Arrays.asList(getDummyReviewDtoOne(), getDummyReviewDtoTwo());

        when(reviewService.getReviewsByRating(rating)).thenReturn(reviewDtoList);

        mockMvc.perform(get("/review/rating/{rating}", rating)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testCreateReview() throws Exception {
        ReviewDto reviewDto = getDummyReviewDtoOne();

        when(reviewService.saveReview(any())).thenReturn(reviewDto);

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/review/" + reviewDto.getReviewId()));
    }

    @Test
    public void testCreateReviewInvalid() throws Exception {
        ReviewDto reviewDto = getDummyReviewDtoOne();
        reviewDto.setRating(0);

        when(reviewService.saveReview(any())).thenReturn(reviewDto);

        mockMvc.perform(post("/review")
                        .content(objectMapper.writeValueAsString(reviewDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateReviewSuccess() throws Exception {
        ReviewDto reviewDto = getDummyReviewDtoOne();

        when(reviewService.updateReview(anyLong(), any())).thenReturn(reviewDto);

        mockMvc.perform(put("/review/id/{reviewId}", reviewDto.getReviewId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId", is(reviewDto.getReviewId().intValue())));
    }

    @Test
    public void testUpdateReviewInvalid() throws Exception {
        ReviewDto reviewDto = getDummyReviewDtoOne();
        reviewDto.setRating(0);

        when(reviewService.updateReview(anyLong(), any())).thenReturn(reviewDto);

        mockMvc.perform(put("/review/id/{reviewId}", reviewDto.getReviewId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteReviewSuccess() throws Exception {
        Long reviewId = 10L;

        mockMvc.perform(delete("/review/id/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteReviewNotFound() throws Exception {
        Long reviewId = 10L;

        doThrow(new ReviewNotFoundException(reviewId)).when(reviewService).removeReviewById(reviewId);

        mockMvc.perform(delete("/review/id/{reviewId}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private ReviewDto getDummyReviewDtoOne(){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(10L);
        reviewDto.setCustomerId(getDummyCustomer().getCustomerId());
        reviewDto.setProductId(getDummyProduct().getProductId());
        reviewDto.setRating(5);
        reviewDto.setComment("Review Test Comment");
        reviewDto.setReviewDate(Timestamp.valueOf(LocalDateTime.now()));
        return reviewDto;
    }

    private ReviewDto getDummyReviewDtoTwo(){
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReviewId(11L);
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
