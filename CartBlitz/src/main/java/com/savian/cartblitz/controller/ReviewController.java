package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
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
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<ReviewDto>>> GetAllReviews() {
        List<ReviewDto> reviews = reviewService.getAllReviews();
        List<EntityModel<ReviewDto>> reviewModels = reviews.stream()
                .map(review -> EntityModel.of(review,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(review.getReviewId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetAllReviews()).withRel("all-reviews")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ReviewDto>> collectionModel = CollectionModel.of(reviewModels);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetAllReviews()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/id/{reviewId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a review with given id",
            summary = "Showing review with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<ReviewDto>> GetReviewById(
            @PathVariable
            @Parameter(name = "reviewId", description = "Review id", example = "1", required = true) Long reviewId){
        Optional<ReviewDto> optionalReview = reviewService.getReviewById(reviewId);

        return optionalReview.map(review -> ResponseEntity.ok(EntityModel.of(review,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(reviewId)).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetAllReviews()).withRel("all-reviews"))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/customer/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews written by the customer with the given id",
            summary = "Showing reviews from the given customer",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<ReviewDto>>> GetReviewsByCustomerId(
            @PathVariable
            @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId){
        List<ReviewDto> reviews = reviewService.getReviewsByCustomerId(customerId);
        List<EntityModel<ReviewDto>> reviewModels = reviews.stream()
                .map(review -> EntityModel.of(review,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(review.getReviewId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewsByCustomerId(customerId)).withRel("reviews-by-customer")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ReviewDto>> collectionModel = CollectionModel.of(reviewModels);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewsByCustomerId(customerId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/product/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews for the product with the given id",
            summary = "Showing reviews with the given product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<ReviewDto>>> GetReviewsByProductId(
            @PathVariable
            @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId){
        List<ReviewDto> reviews = reviewService.getReviewsByProductId(productId);
        List<EntityModel<ReviewDto>> reviewModels = reviews.stream()
                .map(review -> EntityModel.of(review,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(review.getReviewId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewsByProductId(productId)).withRel("reviews-by-product")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ReviewDto>> collectionModel = CollectionModel.of(reviewModels);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewsByProductId(productId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/rating/{rating}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about reviews with the given rating",
            summary = "Showing reviews with the given rating",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<ReviewDto>>> GetReviewsByRating(
            @PathVariable
            @Parameter(name = "rating", description = "Rating", example = "1", required = true) Integer rating){
        List<ReviewDto> reviews = reviewService.getReviewsByRating(rating);
        List<EntityModel<ReviewDto>> reviewModels = reviews.stream()
                .map(review -> EntityModel.of(review,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(review.getReviewId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewsByRating(rating)).withRel("reviews-by-rating")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ReviewDto>> collectionModel = CollectionModel.of(reviewModels);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewsByRating(rating)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping(path = "/api", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating review - all info will be put in",
            summary = "Creating a new review",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<EntityModel<ReviewDto>> CreateReview(
            @Valid @RequestBody ReviewDto reviewDto){
        ReviewDto review = reviewService.saveReview(reviewDto);
        EntityModel<ReviewDto> reviewModel = EntityModel.of(review,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(review.getReviewId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).CreateReview(reviewDto)).withRel("create-review"));

        return ResponseEntity.created(URI.create("/review/" + review.getReviewId())).body(reviewModel);
    }

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating review - all info will be put in",
            summary = "Creating a new review")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "Field validation error", responseCode = "400"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
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
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Review Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<ReviewDto>> updateReview(@PathVariable @Parameter(name = "reviewId", description = "Review id", example = "1", required = true) Long reviewId,
                                                               @Valid @RequestBody ReviewDto reviewDto){
        ReviewDto updatedReview = reviewService.updateReview(reviewId, reviewDto);
        EntityModel<ReviewDto> reviewModel = EntityModel.of(updatedReview,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).GetReviewById(reviewId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ReviewController.class).updateReview(reviewId, reviewDto)).withRel("update-review"));

        return ResponseEntity.ok(reviewModel);
    }

    @DeleteMapping(path = "/id/{reviewId}")
    @Operation(description = "Deleting a review with a given id",
            summary = "Deleting a review with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Void> DeleteReview(@PathVariable @Parameter(name = "reviewId",description = "Review id",example = "1",required = true) Long reviewId) {
        try {
            reviewService.removeReviewById(reviewId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
