package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("product")
@Tag(name = "Products",description = "Endpoint manage Products")
public class ProductController {
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products including all fields",
            summary = "Showing all products",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ProductDto>> GetAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping(path = "/id/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a product with given id",
            summary = "Showing product with given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<Optional<Product>> GetProductById(
            @PathVariable
            @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId){
        Optional<Product> optionalProduct = productService.getProductById(productId);

        if (optionalProduct.isPresent()) {
            return ResponseEntity.ok(optionalProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/category/{category}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products from the given category",
            summary = "Showing products with from the given category",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ProductDto>> GetProductsByCategory(
            @PathVariable
            @Parameter(name = "category", description = "Product category", example = "CPU", required = true) String category){
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping(path = "/brand/{brand}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products from the given brand",
            summary = "Showing products with from the given brand",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
            })
    public ResponseEntity<List<ProductDto>> GetProductsByBrand(
            @PathVariable
            @Parameter(name = "brand", description = "Product brand", example = "Intel", required = true) String brand){
        return ResponseEntity.ok(productService.getProductsByBrand(brand));
    }

    @GetMapping(path = "/priceRange", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products in the given price range",
            summary = "Showing products with from the given price range",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<List<ProductDto>> GetProductsByPriceRange(
            @RequestParam(name = "minPrice", required = false, defaultValue = "0.00") BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "100000000.00") BigDecimal maxPrice){
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
    }

    @PatchMapping
    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Update the stock quantity for a given product",
            summary = "Update stock quantity",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<Product> UpdateStockQuantity(
            @RequestParam Long productId,
            @RequestParam Integer stockQuantity){
        return ResponseEntity.ok(productService.updateStockQuantity(productId, stockQuantity));
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating product - all info will be put in",
            summary = "Creating a new product",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Bad Request - validation error per request",
                            responseCode = "500"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<Product> CreateProduct(
            @Valid @RequestBody ProductDto productDto){
        Product product = productService.saveProduct(productDto);
        return ResponseEntity.created(URI.create("/product/" + product.getProductId())).body(product);
    }

    @PutMapping(path = "/id/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a product with the given id",
            summary = "Updating product with given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Product Not Found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<Product> UpdateProduct(@PathVariable @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId,
                                                   @Valid @RequestBody ProductDto productDto){
        return  ResponseEntity.ok(productService.updateProduct(productId, productDto));
    }

    @DeleteMapping(path = "/id/{productId}")
    @Operation(description = "Deleting a product with a given id",
            summary = "Deleting a product with a given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Product Not Found",
                            responseCode = "404"
                    ),
            })
    public void DeleteProduct(@PathVariable @Parameter(name = "productId",description = "Product id",example = "1",required = true) Long productId) {
        productService.removeProductById(productId);
    }
}
