package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.TagNotFoundException;
import com.savian.cartblitz.mapper.ProductMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.model.Tag;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("h2")
@Slf4j
public class ProductServiceUnitTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private ProductMapper productMapper;

    @Test
    public void testGetAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(getDummyProduct());

        log.info("Starting testGetAllProducts");

        Mockito.when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> result = productService.getAllProducts();
        products.forEach(product -> log.info(String.valueOf(product.getProductId())));

        Mockito.verify(productRepository).findAll();
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);

        log.info("Finished testGetAllProducts successfully");
    }

    @Test
    public void testGetProductByIdFound() {
        Product product = getDummyProduct();

        log.info("Starting testGetProductByIdFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(product.getProductId());
        result.ifPresent(value -> log.info(String.valueOf(value.getProductId())));

        Mockito.verify(productRepository).findById(product.getProductId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(product, result.get());

        log.info("Finished testGetProductByIdFound successfully");
    }

    @Test
    public void testGetProductByIdNotFound() {
        Product product = getDummyProduct();

        log.info("Starting testGetProductByIdNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.getProductById(product.getProductId()));
        log.error("Product with given ID was not found");

        Mockito.verify(productRepository).findById(product.getProductId());

        log.info("Finished testGetProductByIdNotFound successfully");
    }

    @Test
    public void testGetProductsByCategory() {
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        products.add(product);

        log.info("Starting testGetProductsByCategory");

        Mockito.when(productRepository.findByCategoryIgnoreCase(Mockito.anyString())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByCategory(product.getCategory());
        products.forEach(product1 -> log.info(String.valueOf(product1.getProductId())));

        Mockito.verify(productRepository).findByCategoryIgnoreCase(product.getCategory());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);

        log.info("Finished testGetProductsByCategory successfully");
    }

    @Test
    public void testGetProductsByBrand() {
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        products.add(product);

        log.info("Starting testGetProductsByBrand");

        Mockito.when(productRepository.findByBrandIgnoreCase(Mockito.anyString())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByBrand(product.getBrand());
        products.forEach(product1 -> log.info(String.valueOf(product1.getProductId())));

        Mockito.verify(productRepository).findByBrandIgnoreCase(product.getBrand());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);

        log.info("Finished testGetProductsByBrand successfully");
    }

    @Test
    public void testGetProductsByPriceRange() {
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        products.add(product);

        log.info("Starting testGetProductsByPriceRange");

        Mockito.when(productRepository.findByPriceBetween(Mockito.any(), Mockito.any())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByPriceRange(product.getPrice(), product.getPrice());
        products.forEach(product1 -> log.info(String.valueOf(product1.getProductId())));

        Mockito.verify(productRepository).findByPriceBetween(product.getPrice(), product.getPrice());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);

        log.info("Finished testGetProductsByPriceRange successfully");
    }

    @Test
    public void testGetProductsByTagIdNotFound(){
        Product product = getDummyProduct();
        Tag tag = getDummyTag();
        product.setTags(Collections.singletonList(tag));

        log.info("Starting testGetProductsByTagIdNotFound");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(TagNotFoundException.class, () -> productService.getProductsByTagId(tag.getTagId()));
        log.error("Tag with given id was not found");

        Mockito.verify(productRepository, Mockito.never()).findByTagsTagId(Mockito.anyLong());

        log.info("Finished testGetProductsByTagIdNotFound successfully");
    }

    @Test
    public void testGetProductsByTagIdFound(){
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        Tag tag = getDummyTag();
        product.setTags(Collections.singletonList(tag));
        products.add(product);

        log.info("Starting testGetProductsByTagIdFound");

        Mockito.when(tagRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tag));
        Mockito.when(productRepository.findByTagsTagId(Mockito.anyLong())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByTagId(tag.getTagId());
        products.forEach(product1 -> log.info(String.valueOf(product1.getProductId())));

        Mockito.verify(productRepository).findByTagsTagId(tag.getTagId());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);

        log.info("Finished testGetProductsByTagIdFound successfully");
    }

    @Test
    public void testUpdateStockQuantitySuccess() {
        Product existingProduct = getDummyProduct();

        log.info("Starting testUpdateStockQuantitySuccess");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingProduct));
        
        Product updatedProduct = getDummyProduct();
        ProductDto updatedProductDto = getDummyProductDto();

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateStockQuantity(existingProduct.getProductId(), updatedProductDto.getStockQuantity());
        log.info(String.valueOf(result.getProductId()));

        Mockito.verify(productRepository).findById(existingProduct.getProductId());
        Mockito.verify(productRepository).save(updatedProduct);
        Assertions.assertEquals(updatedProduct, result);

        log.info("Finished testUpdateStockQuantitySuccess successfully");
    }


    @Test
    public void testUpdateStockQuantityNotFound() {
        Product existingProduct = getDummyProduct();
        ProductDto updatedProductDto = getDummyProductDto();

        log.info("Starting testUpdateStockQuantityNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.updateStockQuantity(existingProduct.getProductId(), updatedProductDto.getStockQuantity()));
        log.error("Product with given ID was not found");

        Mockito.verify(productRepository).findById(existingProduct.getProductId());

        log.info("Finished testUpdateStockQuantityNotFound successfully");
    }

    @Test
    public void testSaveProductSuccess() {
        ProductDto productDto = getDummyProductDto();
        Product savedProductEntity = getDummyProduct();

        log.info("Starting testSaveProductSuccess");

        Mockito.when(productRepository.save(productMapper.productDtoToProduct(Mockito.any(ProductDto.class)))).thenReturn(savedProductEntity);

        Product result = productService.saveProduct(productDto);
        log.info(String.valueOf(result.getProductId()));

        Mockito.verify(productRepository).save(productMapper.productDtoToProduct(productDto));
        Assertions.assertEquals(savedProductEntity, result);

        log.info("Finished testSaveProductSuccess successfully");
    }

    @Test
    public void testUpdateProductSuccess() {
        Product existingProduct = getDummyProduct();

        log.info("Starting testUpdateProductSuccess");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingProduct));

        Product updatedProduct = getDummyProduct();
        ProductDto updatedProductDto = getDummyProductDto();

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(existingProduct.getProductId(), updatedProductDto);
        log.info(String.valueOf(result.getProductId()));

        Mockito.verify(productRepository).findById(existingProduct.getProductId());
        Mockito.verify(productRepository).save(updatedProduct);
        Assertions.assertEquals(updatedProduct, result);

        log.info("Finished testUpdateProductSuccess successfully");
    }

    @Test
    public void testUpdateProductNotFound() {
        ProductDto productDto = getDummyProductDto();

        log.info("Starting testUpdateProductNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productDto.getProductId(), productDto));
        log.error("Product with given ID was not found");

        Mockito.verify(productRepository).findById(productDto.getProductId());

        log.info("Finished testUpdateProductNotFound successfully");
    }

    @Test
    public void testRemoveProductByIdSuccess() {
        Product product = getDummyProduct();

        log.info("Starting testRemoveProductByIdSuccess");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));

        productService.removeProductById(product.getProductId());
        log.info(String.valueOf(product.getProductId()));

        Mockito.verify(productRepository).findById(product.getProductId());
        Mockito.verify(productRepository).deleteById(product.getProductId());

        log.info("Finished testRemoveProductByIdSuccess successfully");
    }

    @Test
    public void testRemoveProductByIdNotFound() {
        Product product = getDummyProduct();

        log.info("Starting testRemoveProductByIdNotFound");

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.removeProductById(product.getProductId()));
        log.error("Product with given ID was not found");

        Mockito.verify(productRepository).findById(product.getProductId());

        log.info("Finished testRemoveProductByIdNotFound successfully");
    }

    private Product getDummyProduct(){
        Product product = new Product();
        product.setProductId(10L);
        product.setName("productTest");
        product.setPrice(BigDecimal.valueOf(0L));
        product.setStockQuantity(0);
        product.setDescription("productTest description");
        product.setBrand("productTest brand");
        product.setCategory("productTest category");
        return product;
    }

    private ProductDto getDummyProductDto(){
        ProductDto productDto = new ProductDto();
        productDto.setProductId(10L);
        productDto.setName("productTest");
        productDto.setPrice(BigDecimal.valueOf(0L));
        productDto.setStockQuantity(0);
        productDto.setDescription("productTest description");
        productDto.setBrand("productTest brand");
        productDto.setCategory("productTest category");
        return productDto;
    }

    private Tag getDummyTag(){
        Tag tag = new Tag();
        tag.setTagId(10L);
        tag.setName("name");
        return tag;
    }
}
