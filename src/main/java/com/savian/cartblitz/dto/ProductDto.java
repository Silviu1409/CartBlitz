package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long productId;

    @NotNull(message = "Name is mandatory.")
    @NotBlank(message = "Name must have a value.")
    @Size(min = 3, max = 50, message = "Product name must be between 3 and 50 characters long")
    private String name;
    @NotNull(message = "Product price is mandatory.")
    @Positive(message = "Product price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Product price must have up to 8 digits before and 2 digits after the decimal point")
    private BigDecimal price;
    @NotNull(message = "Stock quantity is mandatory.")
    @PositiveOrZero(message = "Stock quantity must be positive or zero")
    private Integer stockQuantity;
    private String description;
    @Size(min = 2, max = 50, message = "Product brand must be between 2 and 50 characters long")
    private String brand;
    @Size(min = 3, max = 50, message = "Product category must be between 3 and 50 characters long")
    private String category;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<OrderProductDto> orderProducts;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ReviewDto> reviews;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<WarrantyDto> warranties;

    public ProductDto() {}
}
