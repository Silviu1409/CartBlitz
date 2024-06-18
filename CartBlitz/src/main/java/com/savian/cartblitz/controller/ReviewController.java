package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
@RequestMapping("review")
@Tag(name = "Reviews",description = "Endpoint manage Reviews")
public class ReviewController {
    ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews including all fields",
            summary = "Showing all reviews",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ReviewDto>> GetAllReviews(){
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping(path = "/id/{reviewId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a review with given id",
            summary = "Showing review with given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<Optional<ReviewDto>> GetReviewById(
            @PathVariable
            @Parameter(name = "reviewId", description = "Review id", example = "1", required = true) Long reviewId){
        Optional<ReviewDto> optionalReview = reviewService.getReviewById(reviewId);

        if (optionalReview.isPresent()) {
            return ResponseEntity.ok(optionalReview);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/customer/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews written by the customer with the given id",
            summary = "Showing reviews from the given customer",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ReviewDto>> GetReviewsByCustomerId(
            @PathVariable
            @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId){
        return ResponseEntity.ok(reviewService.getReviewsByCustomerId(customerId));
    }

    @GetMapping(path = "/product/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews for the product with the given id",
            summary = "Showing reviews with the given product",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ReviewDto>> GetReviewsByProductId(
            @PathVariable
            @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId){
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @GetMapping(path = "/rating/{rating}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews with the given rating",
            summary = "Showing reviews with the given rating",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ReviewDto>> GetReviewsByRating(
            @PathVariable
            @Parameter(name = "rating", description = "Rating", example = "1", required = true) Integer rating){
        return ResponseEntity.ok(reviewService.getReviewsByRating(rating));
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating review - all info will be put in",
            summary = "Creating a new review",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Bad Request - validation error per request",
                            responseCode = "500"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<ReviewDto> CreateReview(
            @Valid @RequestBody ReviewDto reviewDto){
        ReviewDto review = reviewService.saveReview(reviewDto);
        return ResponseEntity.created(URI.create("/review/" + review.getReviewId())).body(review);
    }

    @PostMapping("")
    @Operation(description = "Creating review - all info will be put in",
            summary = "Creating a new review")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500"),
            @ApiResponse(description = "Field validation error", responseCode = "400"),
    })
    public String  CreateReviewRedirectToProduct(
            @Valid @ModelAttribute("review") ReviewDto reviewDto, BindingResult result){
        if (result.hasErrors()) {
            return "error";
        }

        reviewService.saveReview(reviewDto);

        return "redirect:/product/id/" + reviewDto.getProductId();
    }

    @PutMapping(path = "/id/{reviewId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a review with the given id",
            summary = "Updating review with given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Review Not Found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<ReviewDto> UpdateReview(@PathVariable @Parameter(name = "reviewId", description = "Review id", example = "1", required = true) Long reviewId,
                                                 @Valid @RequestBody ReviewDto reviewDto){
        return ResponseEntity.ok(reviewService.updateReview(reviewId, reviewDto));
    }

    @DeleteMapping(path = "/id/{reviewId}")
    @Operation(description = "Deleting a review with a given id",
            summary = "Deleting a review with a given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Review Not Found",
                            responseCode = "404"
                    ),
            })
    public void DeleteReview(@PathVariable @Parameter(name = "reviewId",description = "Review id",example = "1",required = true) Long reviewId) {
        reviewService.removeReviewById(reviewId);
    }
}
