package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ReviewDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long reviewId;

    private Long customerId;
    private Long productId;

    @NotNull(message = "Product rating is mandatory.")
    @Min(value = 1, message = "Product rating must be at min 1")
    @Max(value = 5, message = "Product rating must be at max 5")
    private Integer rating;
    private String comment;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Timestamp reviewDate;

    public ReviewDto() {}
}
