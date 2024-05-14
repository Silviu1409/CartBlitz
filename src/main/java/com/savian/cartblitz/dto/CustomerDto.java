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
    
    @NotNull(message = "Username-ul este obligatoriu.")
    @NotBlank(message = "Username-ul trebuie să aibă o valoare.")
    @Size(min = 5, max = 50, message = "Username trebuie să aibă între 5 și 50 de caractere.")
    private String username;
    @NotNull(message = "Parola este obligatorie.")
    @NotBlank(message = "Parola trebuie să aibă o valoare.")
    @Size(min = 8, max = 255, message = "Parola trebuie să aibă între 8 și 255 de caractere.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Parola trebuie să conțină cel puțin o literă minusculă, o literă majusculă, o cifră și un caracter special."
    )
    private String password;
    @NotNull(message = "Email-ul este obligatoriu.")
    @NotBlank(message = "Email-ul trebuie să aibă o valoare.")
    @Size(max = 100, message = "Adresa de email nu trebuie să depășească 100 de caractere.")
    @Email(message = "Adresa de email nu este validă.")
    private String email;
    @NotNull(message = "Numele complet este obligatoriu.")
    @NotBlank(message = "Numele complet trebuie să aibă o valoare.")
    @Size(min = 3, max = 50, message = "Numele complet trebuie să aibă între 3 și 50 de caractere.")
    @Pattern(
            regexp = "^[a-zA-Z'\\-\\s]+$",
            message = "Numele complet trebuie să conțină doar litere, spații, apostrofuri și cratime."
    )
    private String fullName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<OrderDto> orders;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ReviewDto> reviews;

    public CustomerDto() {}
}
