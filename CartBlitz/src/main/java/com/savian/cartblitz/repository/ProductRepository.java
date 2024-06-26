package com.savian.cartblitz.repository;

import com.savian.cartblitz.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTagsTagId(Long tagId);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByBrandIgnoreCase(String brand);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByBrandContainingIgnoreCase(String brand);
    List<Product> findByCategoryContainingIgnoreCase(String category);
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByTagsNameIgnoreCase(String tag);
}
