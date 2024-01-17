package com.savian.cartblitz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    public Warranty() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warranty warranty = (Warranty) o;
        return Objects.equals(warrantyId, warranty.warrantyId) && Objects.equals(order, warranty.order) && Objects.equals(product, warranty.product) && Objects.equals(durationMonths, warranty.durationMonths);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warrantyId, order, product, durationMonths);
    }

    @Override
    public String toString() {
        return "Warranty{" +
                "warrantyId=" + warrantyId +
                ", order=" + order +
                ", product=" + product +
                ", duration_months=" + durationMonths +
                '}';
    }
}
