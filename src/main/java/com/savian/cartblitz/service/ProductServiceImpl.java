package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.TagNotFoundException;
import com.savian.cartblitz.mapper.ProductMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, TagRepository tagRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::productToProductDto).toList();
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return product;
        }
        else {
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public List<ProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category).stream().map(productMapper::productToProductDto).toList();
    }

    @Override
    public List<ProductDto> getProductsByBrand(String brand) {
        return productRepository.findByBrandIgnoreCase(brand).stream().map(productMapper::productToProductDto).toList();
    }

    @Override
    public List<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream().map(productMapper::productToProductDto).toList();
    }

    @Override
    public List<ProductDto> getProductsByTagId(Long tagId) {
        tagRepository.findById(tagId).orElseThrow(() -> new TagNotFoundException(tagId));

        return productRepository.findByTagsTagId(tagId).stream().map(productMapper::productToProductDto).toList();
    }

    @Override
    public Product updateStockQuantity(Long productId, Integer stockQuantity){
        Optional<Product> optProduct = productRepository.findById(productId);

        if (optProduct.isPresent()){
            Product prevProduct = optProduct.get();
            prevProduct.setStockQuantity(Math.max(stockQuantity, 0));
            return productRepository.save(prevProduct);
        }
        else{
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public Product saveProduct(ProductDto productDto) {
        return productRepository.save(productMapper.productDtoToProduct(productDto));
    }

    @Override
    public Product updateProduct(Long productId, ProductDto productDto) {
        Optional<Product> optProduct = productRepository.findById(productId);

        if (optProduct.isPresent()){
            Product prevProduct = optProduct.get();

            prevProduct.setName(productDto.getName());
            prevProduct.setPrice(productDto.getPrice());
            prevProduct.setStockQuantity(productDto.getStockQuantity());
            prevProduct.setDescription(productDto.getDescription());
            prevProduct.setBrand(productDto.getBrand());
            prevProduct.setCategory(productDto.getCategory());

            return productRepository.save(prevProduct);
        }
        else{
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public void removeProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
            productRepository.deleteById(productId);
        }
        else{
            throw new ProductNotFoundException(productId);
        }
    }
}
