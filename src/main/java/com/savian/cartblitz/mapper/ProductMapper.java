package com.savian.cartblitz.mapper;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.repository.OrderProductRepository;
import com.savian.cartblitz.repository.ReviewRepository;
import com.savian.cartblitz.repository.WarrantyRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    private final OrderProductMapper orderProductMapper;
    private final ReviewMapper reviewMapper;
    private final TagMapper tagMapper;
    private final OrderProductRepository orderProductRepository;
    private final ReviewRepository reviewRepository;
    private final WarrantyRepository warrantyRepository;

    public ProductMapper(OrderProductMapper orderProductMapper, ReviewMapper reviewMapper, TagMapper tagMapper, OrderProductRepository orderProductRepository, ReviewRepository reviewRepository, WarrantyRepository warrantyRepository) {
        this.orderProductMapper = orderProductMapper;
        this.reviewMapper = reviewMapper;
        this.tagMapper = tagMapper;
        this.orderProductRepository = orderProductRepository;
        this.reviewRepository = reviewRepository;
        this.warrantyRepository = warrantyRepository;
    }

    public Product productDtoToProduct(ProductDto productDto){
        Product product = new Product();
        product.setProductId(productDto.getProductId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setDescription(productDto.getDescription());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setOrderProducts(orderProductRepository.findByProductProductId(product.getProductId()));
        product.setReviews(reviewRepository.findByProductProductId(product.getProductId()));
        product.setWarranty(warrantyRepository.getReferenceById(productDto.getWarrantyId()));
        product.setTags(tagMapper.tagDtosToTags(productDto.getTags()));
        return product;
    }

    public ProductDto productToProductDto(Product product){
        ProductDto productDto = new ProductDto();
        productDto.setProductId(product.getProductId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setStockQuantity(product.getStockQuantity());
        productDto.setDescription(product.getDescription());
        productDto.setBrand(product.getBrand());
        productDto.setCategory(product.getCategory());
        productDto.setOrderProducts(product.getOrderProducts().stream().map(orderProductMapper::orderProductToOrderProductDto).toList());
        productDto.setReviews(product.getReviews().stream().map(reviewMapper::reviewToReviewDto).toList());
        productDto.setWarrantyId(product.getWarranty().getWarrantyId());
        productDto.setTags(tagMapper.tagsToTagDtos(product.getTags()));
        return productDto;
    }
}
