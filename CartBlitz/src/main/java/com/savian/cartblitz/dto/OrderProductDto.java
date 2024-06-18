package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderProductDto {
    private Long orderId;
    private Long productId;

    @NotNull(message = "Quantity is mandatory.")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal price;

    public OrderProductDto() {}
}
