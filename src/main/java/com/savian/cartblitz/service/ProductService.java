package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> getAllProducts();
    Optional<Product> getProductById(Long productId);

    List<ProductDto> getProductsByCategory(String category);
    List<ProductDto> getProductsByBrand(String brand);
    List<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductDto> getProductsByTagId(Long tagId);
    Product updateStockQuantity(Long productId, Integer stockQuantity);

    Product saveProduct(ProductDto productDto);
    Product updateProduct(Long productId, ProductDto productDto);
    void removeProductById(Long productId);
}
