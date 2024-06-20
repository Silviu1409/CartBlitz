package com.savian.cartblitz.config;

import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.model.Warranty;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class WarrantyValidator implements Validator {

    @Override
    public boolean supports(@Nonnull Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    @Override
    public void validate(@Nonnull Object target, @Nonnull Errors errors) {
        Product product = (Product) target;
        Warranty warranty = product.getWarranty();

        if (warranty != null) {
            if (!areAllWarrantyFieldsEmpty(warranty) && !areAllWarrantyFieldsCompleted(warranty)) {
                errors.reject("warranty", "Câmpurile legate de garanție trebuie să fie toate completate sau toate necompletate.");
            }
        }
    }

    public boolean areAllWarrantyFieldsEmpty(Warranty warranty) {
        return warranty.getDurationMonths() == null && warranty.getType().isEmpty() && warranty.getType().isBlank() && warranty.getTerms().isEmpty() && warranty.getTerms().isBlank() && warranty.getDetails().isEmpty() && warranty.getDetails().isBlank();
    }

    public boolean areAllWarrantyFieldsCompleted(Warranty warranty) {
        return warranty.getDurationMonths() != null && !warranty.getType().isEmpty() && !warranty.getType().isBlank() && !warranty.getTerms().isEmpty() && !warranty.getTerms().isBlank() && !warranty.getDetails().isEmpty() && !warranty.getDetails().isBlank();
    }
}
