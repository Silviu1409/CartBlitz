package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarrantyDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long warrantyId;

    private Long orderId;
    private Long productId;

    @NotNull(message = "Warranty duration is mandatory.")
    @PositiveOrZero(message = "Warranty duration must be positive or zero")
    private Integer durationMonths;

    public WarrantyDto() {}
}
