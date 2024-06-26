package com.savian.cartblitz.service;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.exception.ProductNotFoundException;
import com.savian.cartblitz.exception.TagNotFoundException;
import com.savian.cartblitz.mapper.ProductMapper;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.repository.ProductRepository;
import com.savian.cartblitz.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<ProductDto> sortProducts(List<ProductDto> products, String sortBy, String sortOrder) {
        Comparator<ProductDto> comparator;
        switch (sortBy) {
            case "brand":
                comparator = Comparator.comparing(ProductDto::getBrand);
                break;
            case "name":
                comparator = Comparator.comparing(ProductDto::getName);
                break;
            case "price":
                comparator = Comparator.comparing(ProductDto::getPrice);
                break;
            default:
                return products;
        }

        if (sortOrder.equals("desc")) {
            comparator = comparator.reversed();
        }

        List<ProductDto> sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(comparator);

        return sortedProducts;
    }

    @Override
    public List<ProductDto> filterProductsMinPriceMaxPrice(List<ProductDto> products, BigDecimal minPrice, BigDecimal maxPrice) {
        List<ProductDto> filteredProducts = new ArrayList<>();

        for (ProductDto product : products) {
            BigDecimal price = product.getPrice();

            if (price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    @Override
    public List<ProductDto> searchProducts(String search) {
        List<Product> byBrand = productRepository.findByBrandContainingIgnoreCase(search);
        List<Product> byCategory = productRepository.findByCategoryContainingIgnoreCase(search);
        List<Product> byDescription = productRepository.findByDescriptionContainingIgnoreCase(search);
        List<Product> byName = productRepository.findByNameContainingIgnoreCase(search);
        List<Product> byTag = productRepository.findByTagsNameIgnoreCase(search);

        Set<Product> resultSet = new HashSet<>();
        resultSet.addAll(byBrand);
        resultSet.addAll(byCategory);
        resultSet.addAll(byDescription);
        resultSet.addAll(byName);
        resultSet.addAll(byTag);

        return resultSet.stream()
                .map(productMapper::productToProductDto)
                .collect(Collectors.toList());
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
    public int getNumImagesForProduct(String category, Long productId) {
        Optional<Product> optProduct = productRepository.findById(productId);

        if (optProduct.isPresent()) {
            String productDir = "src/main/resources/static/images/products/" + category + "/" + productId;

            int numImages = 0;

            while (true) {
                String imagePath = productDir + "_" + (numImages + 1) + ".jpg";

                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    numImages++;
                }
                else {
                    break;
                }
            }

            return numImages;
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
