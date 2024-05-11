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
    List<ProductDto> sortProducts(List<ProductDto> products, String sortBy, String sortOrder);
    List<ProductDto> filterProductsMinPriceMaxPrice(List<ProductDto> products, BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductDto> searchProducts(String search);
    Product updateStockQuantity(Long productId, Integer stockQuantity);

    Product saveProduct(ProductDto productDto);
    Product updateProduct(Long productId, ProductDto productDto);
    void removeProductById(Long productId);
}
