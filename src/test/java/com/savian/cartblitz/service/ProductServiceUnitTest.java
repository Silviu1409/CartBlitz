package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.mapper.ProductMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @Test
    public void testGetAllProducts() {
        List<Product> products = new ArrayList<>();
        Mockito.when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> result = productService.getAllProducts();

        Mockito.verify(productRepository).findAll();
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);
    }

    @Test
    public void testGetProductByIdFound() {
        Product product = getDummyProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(product.getProductId());

        Mockito.verify(productRepository).findById(product.getProductId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(product, result.get());
    }

    @Test
    public void testGetProductByIdNotFound() {
        Product product = getDummyProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.getProductById(product.getProductId()));

        Mockito.verify(productRepository).findById(product.getProductId());
    }

    @Test
    public void testGetProductsByCategory() {
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        products.add(product);

        Mockito.when(productRepository.findByCategoryIgnoreCase(Mockito.anyString())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByCategory(product.getCategory());

        Mockito.verify(productRepository).findByCategoryIgnoreCase(product.getCategory());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);
    }

    @Test
    public void testGetProductsByBrand() {
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        products.add(product);

        Mockito.when(productRepository.findByBrandIgnoreCase(Mockito.anyString())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByBrand(product.getBrand());

        Mockito.verify(productRepository).findByBrandIgnoreCase(product.getBrand());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);
    }

    @Test
    public void testGetProductsByPriceRange() {
        List<Product> products = new ArrayList<>();
        Product product = getDummyProduct();
        products.add(product);

        Mockito.when(productRepository.findByPriceBetween(Mockito.any(), Mockito.any())).thenReturn(products);

        List<ProductDto> result = productService.getProductsByPriceRange(product.getPrice(), product.getPrice());

        Mockito.verify(productRepository).findByPriceBetween(product.getPrice(), product.getPrice());
        Assertions.assertEquals(products.stream().map(productMapper::productToProductDto).toList(), result);
    }

    @Test
    public void testUpdateStockQuantitySuccess() {
        Product existingProduct = getDummyProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingProduct));
        
        Product updatedProduct = getDummyProduct();
        ProductDto updatedProductDto = getDummyProductDto();

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateStockQuantity(existingProduct.getProductId(), updatedProductDto.getStockQuantity());

        Mockito.verify(productRepository).findById(existingProduct.getProductId());
        Mockito.verify(productRepository).save(updatedProduct);
        Assertions.assertEquals(updatedProduct, result);
    }


    @Test
    public void testUpdateStockQuantityNotFound() {
        Product existingProduct = getDummyProduct();
        ProductDto updatedProductDto = getDummyProductDto();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.updateStockQuantity(existingProduct.getProductId(), updatedProductDto.getStockQuantity()));

        Mockito.verify(productRepository).findById(existingProduct.getProductId());
    }

    @Test
    public void testSaveProductSuccess() {
        ProductDto productDto = getDummyProductDto();
        Product savedProductEntity = getDummyProduct();

        Mockito.when(productRepository.save(productMapper.productDtoToProduct(Mockito.any(ProductDto.class)))).thenReturn(savedProductEntity);

        Product result = productService.saveProduct(productDto);

        Mockito.verify(productRepository).save(productMapper.productDtoToProduct(productDto));
        Assertions.assertEquals(savedProductEntity, result);
    }

    @Test
    public void testUpdateProductSuccess() {
        Product existingProduct = getDummyProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(existingProduct));

        Product updatedProduct = getDummyProduct();
        ProductDto updatedProductDto = getDummyProductDto();

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(existingProduct.getProductId(), updatedProductDto);

        Mockito.verify(productRepository).findById(existingProduct.getProductId());
        Mockito.verify(productRepository).save(updatedProduct);
        Assertions.assertEquals(updatedProduct, result);
    }

    @Test
    public void testUpdateProductNotFound() {
        ProductDto productDto = getDummyProductDto();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productDto.getProductId(), productDto));

        Mockito.verify(productRepository).findById(productDto.getProductId());
    }

    @Test
    public void testRemoveProductByIdSuccess() {
        Product product = getDummyProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));

        productService.removeProductById(product.getProductId());

        Mockito.verify(productRepository).findById(product.getProductId());
        Mockito.verify(productRepository).deleteById(product.getProductId());
    }

    @Test
    public void testRemoveProductByIdNotFound() {
        Product product = getDummyProduct();

        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.removeProductById(product.getProductId()));

        Mockito.verify(productRepository).findById(product.getProductId());
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
}
