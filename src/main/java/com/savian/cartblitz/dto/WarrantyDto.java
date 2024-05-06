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

    @NotNull(message = "Warranty duration is mandatory.")
    @PositiveOrZero(message = "Warranty duration must be positive or zero")
    private Integer durationMonths;

    @NotBlank(message = "Type is mandatory.")
    private String type;

    @NotBlank(message = "Terms are mandatory.")
    private String terms;

    @NotBlank(message = "Details are mandatory.")
    private String details;

    public WarrantyDto() {}
}
