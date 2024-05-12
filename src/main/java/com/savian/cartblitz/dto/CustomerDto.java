package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long customerId;
    
    @NotNull(message = "Username is mandatory.")
    @NotBlank(message = "Username must have a value.")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters long")
    private String username;
    @NotNull(message = "Password is mandatory.")
    @NotBlank(message = "Password must have a value.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    private String password;
    @NotNull(message = "Email is mandatory.")
    @NotBlank(message = "Email must have a value.")
    @Size(max = 100, message = "Email address must not exceed 100 characters")
    @Email(message = "Invalid email address")
    private String email;
    @NotNull(message = "Full name is mandatory.")
    @NotBlank(message = "Full name must have a value.")
    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z'\\-\\s]+$",
            message = "Full name must contain only letters, spaces, apostrophes, and hyphens"
    )
    private String fullName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<OrderDto> orders;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ReviewDto> reviews;

    public CustomerDto() {}
}
