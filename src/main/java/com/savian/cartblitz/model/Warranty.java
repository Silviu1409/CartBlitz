package com.savian.cartblitz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "warranty")
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warrantyId;

    @OneToOne(mappedBy = "warranty", cascade = CascadeType.ALL)
    private Product product;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "terms", nullable = false)
    private String terms;

    @Column(name = "details", nullable = false)
    private String details;

    public Warranty() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warranty warranty = (Warranty) o;
        return Objects.equals(warrantyId, warranty.warrantyId) && Objects.equals(product, warranty.product) && Objects.equals(durationMonths, warranty.durationMonths) && Objects.equals(type, warranty.type) && Objects.equals(terms, warranty.terms) && Objects.equals(details, warranty.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warrantyId, product, durationMonths, type, terms, details);
    }

    @Override
    public String toString() {
        return "Warranty{" +
                "warrantyId=" + warrantyId +
                ", product=" + product +
                ", durationMonths=" + durationMonths +
                ", type='" + type + '\'' +
                ", terms='" + terms + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
