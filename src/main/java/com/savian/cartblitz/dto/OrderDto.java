package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.savian.cartblitz.model.OrderStatusEnum;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long orderId;

    private Long customerId;

    @NotNull(message = "Total amount is mandatory.")
    @Positive(message = "Total amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Total amount must have up to 8 digits before and 2 digits after the decimal point")
    private BigDecimal totalAmount;
    @NotNull(message = "Order status is mandatory.")
    @NotBlank(message = "Order status must have a value.")
    @Pattern(regexp = "CART|COMPLETED", message = "Order status must be either 'CART' or 'COMPLETED'")
    private OrderStatusEnum status;
    @Past(message = "Order date must be in the past")
    private Timestamp orderDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<OrderProductDto> orderProducts;

    public OrderDto() {}
}
