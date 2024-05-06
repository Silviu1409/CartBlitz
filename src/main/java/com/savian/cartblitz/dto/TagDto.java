package com.savian.cartblitz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tagId;

    @NotNull(message = "Name is mandatory.")
    @NotBlank(message = "Name must have a value.")
    @Size(min = 3, max = 50, message = "Tag name must be between 3 and 50 characters long")
    private String name;

    public TagDto() {}
}
