package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.dto.ProductDto;
import com.savian.cartblitz.dto.ReviewDto;
import com.savian.cartblitz.model.*;
import com.savian.cartblitz.repository.OrderProductRepository;
import com.savian.cartblitz.service.CustomerService;
import com.savian.cartblitz.service.OrderProductService;
import com.savian.cartblitz.service.OrderService;
import com.savian.cartblitz.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.security.Principal;
import java.util.*;

@Controller
@Validated
@RequestMapping("product")
@Tag(name = "Products",description = "Endpoint manage Products")
public class ProductController {
    private ProductService productService;
    private CustomerService customerService;
    private OrderService orderService;
    private OrderProductRepository orderProductRepository;
    private OrderProductService orderProductService;

    public ProductController(ProductService productService, CustomerService customerService, OrderService orderService, OrderProductRepository orderProductRepository, OrderProductService orderProductService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.orderProductRepository = orderProductRepository;
        this.orderProductService = orderProductService;
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

    @GetMapping(path = "/id/{productId}")
    @Operation(description = "Showing all info about a product with given id",
            summary = "Showing product with given id")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public String GetProductById(
            @PathVariable Long productId, Model model, Principal principal){
        Optional<Product> optionalProduct = productService.getProductById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            ReviewDto review = new ReviewDto();

            if(principal != null){
                String username = principal.getName();
                Optional<Customer> optionalCustomer = customerService.getCustomerByUsername(username);

                optionalCustomer.ifPresent(customer -> review.setCustomerId(customer.getCustomerId()));
            }

            int numImages = productService.getNumImagesForProduct(product.getCategory().toLowerCase(), productId);

            List<String> tagNames = new ArrayList<>();
            for (com.savian.cartblitz.model.Tag tag : product.getTags()) {
                tagNames.add(tag.getName());
            }

            review.setProductId(productId);

            model.addAttribute("product", product);
            model.addAttribute("numImages", numImages);
            model.addAttribute("review", review);
            model.addAttribute("tagNames", tagNames);

            return "product";
        } else {
            return "error";
        }
    }

    @GetMapping(path = "/category/{category}")
    @Operation(description = "Showing all info about products from the given category",
            summary = "Showing products with from the given category")
    @ApiResponses(value = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public String GetProductsByCategory(
            @PathVariable String category,
            HttpSession session,
            Model model){
        List<ProductDto> products = productService.getProductsByCategory(category);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for(ProductDto productDto: products){
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        session.setAttribute("products", products);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("categoryReadable", getReadableCategory(category));
        model.addAttribute("numImagesMap", numImagesMap);

        return "products";
    }

    @GetMapping(path = "/category/{category}/sort")
    @Operation(description = "Sort products from the given category")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public String SortProductsByCategory(
            @PathVariable String category,
            @RequestParam String sortBy,
            @RequestParam String sortOrder,
            HttpSession session,
            Model model) {
        @SuppressWarnings("unchecked")
        List<ProductDto> products = (List<ProductDto>) session.getAttribute("products");

        if (products == null) {
            return "redirect:/";
        }

        List<ProductDto> sortedProducts = productService.sortProducts(products, sortBy, sortOrder);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for(ProductDto productDto: products){
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        model.addAttribute("products", sortedProducts);
        model.addAttribute("category", category);
        model.addAttribute("categoryReadable", getReadableCategory(category));
        model.addAttribute("numImagesMap", numImagesMap);

        return "products";
    }

    @GetMapping(path = "/category/{category}/filter")
    @Operation(description = "Filter products from the given category by min price and max price")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public String FilterProductsFromCategoryByMinPriceMaxPrice(
            @PathVariable String category,
            @RequestParam String minPrice,
            @RequestParam String maxPrice,
            HttpSession session,
            Model model) {
        @SuppressWarnings("unchecked")
        List<ProductDto> products = (List<ProductDto>) session.getAttribute("products");

        if (products == null) {
            return "redirect:/";
        }

        BigDecimal minPriceValue = BigDecimal.ZERO;
        BigDecimal maxPriceValue = BigDecimal.valueOf(Double.MAX_VALUE);

        if (minPrice != null && !minPrice.isEmpty()) {
            try {
                minPriceValue = new BigDecimal(minPrice);
            } catch (NumberFormatException ignored) {}
        }

        if (maxPrice != null && !maxPrice.isEmpty()) {
            try {
                maxPriceValue = new BigDecimal(maxPrice);
            } catch (NumberFormatException ignored) {}
        }

        List<ProductDto> sortedProducts = productService.filterProductsMinPriceMaxPrice(products, minPriceValue, maxPriceValue);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for(ProductDto productDto: products){
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        model.addAttribute("products", sortedProducts);
        model.addAttribute("category", category);
        model.addAttribute("categoryReadable", getReadableCategory(category));
        model.addAttribute("numImagesMap", numImagesMap);

        return "products";
    }

    @GetMapping(path = "/search")
    @Operation(summary = "Search products", description = "Search for products based on the provided search query")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404")
    })
    public String searchProducts(
            @RequestParam(name = "search") String searchQuery,
            HttpSession session,
            Model model) {
        List<ProductDto> products = productService.searchProducts(searchQuery);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for(ProductDto productDto: products){
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        session.setAttribute("products", products);
        model.addAttribute("products", products);
        model.addAttribute("numImagesMap", numImagesMap);

        return "products";
    }

    @GetMapping(path = "/sort")
    @Operation(description = "Sort products")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public String SortProducts(
            @RequestParam String sortBy,
            @RequestParam String sortOrder,
            HttpSession session,
            Model model) {
        @SuppressWarnings("unchecked")
        List<ProductDto> products = (List<ProductDto>) session.getAttribute("products");

        if (products == null) {
            return "redirect:/";
        }

        List<ProductDto> sortedProducts = productService.sortProducts(products, sortBy, sortOrder);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for(ProductDto productDto: products){
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        model.addAttribute("products", sortedProducts);
        model.addAttribute("numImagesMap", numImagesMap);

        return "products";
    }

    @GetMapping(path = "/filter")
    @Operation(description = "Filter products from the given category by min price and max price")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public String FilterProductsByMinPriceMaxPrice(
            @RequestParam String minPrice,
            @RequestParam String maxPrice,
            HttpSession session,
            Model model) {
        @SuppressWarnings("unchecked")
        List<ProductDto> products = (List<ProductDto>) session.getAttribute("products");

        if (products == null) {
            return "redirect:/";
        }

        BigDecimal minPriceValue = BigDecimal.ZERO;
        BigDecimal maxPriceValue = BigDecimal.valueOf(Double.MAX_VALUE);

        if (minPrice != null && !minPrice.isEmpty()) {
            try {
                minPriceValue = new BigDecimal(minPrice);
            } catch (NumberFormatException ignored) {}
        }

        if (maxPrice != null && !maxPrice.isEmpty()) {
            try {
                maxPriceValue = new BigDecimal(maxPrice);
            } catch (NumberFormatException ignored) {}
        }

        List<ProductDto> sortedProducts = productService.filterProductsMinPriceMaxPrice(products, minPriceValue, maxPriceValue);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for(ProductDto productDto: products){
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        model.addAttribute("products", sortedProducts);
        model.addAttribute("numImagesMap", numImagesMap);

        return "products";
    }

    @GetMapping("/add-to-cart/{productId}")
    public ResponseEntity<String> addToCart(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Optional<Customer> customer = customerService.getCustomerByUsername(username);

        if (customer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        OrderDto shoppingCart = orderService.getOrdersByCustomerIdAndStatus(customer.get().getCustomerId(), OrderStatusEnum.CART).stream().findFirst().orElse(null);

        if (shoppingCart == null) {
            shoppingCart = orderService.saveOrder(customer.get().getCustomerId());
        }

        Optional<OrderProduct> orderProduct = orderProductRepository.findByOrderOrderIdAndProductProductId(shoppingCart.getOrderId(), productId);
        if (orderProduct.isPresent()) {
            orderProduct.get().setQuantity(orderProduct.get().getQuantity() + 1);
            orderProduct.get().setPrice(orderProduct.get().getPrice().add(product.get().getPrice()));
        } else {
            OrderProductDto newOrderProduct = new OrderProductDto(shoppingCart.getOrderId(), productId, 1, product.get().getPrice());
            orderProductService.saveOrderProduct(newOrderProduct);
            shoppingCart.setTotalAmount(product.get().getPrice());
            shoppingCart.setOrderProducts(new ArrayList<>());
            shoppingCart.getOrderProducts().add(newOrderProduct);
        }

        orderService.saveOrUpdateOrder(shoppingCart);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/cart"))
                .build();
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

    @GetMapping(path = "/tag/{tagId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products with the given tag id",
            summary = "Showing products from the given tag id",
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
    public ResponseEntity<List<ProductDto>> GetProductsByTagId(
            @PathVariable
            @Parameter(name = "tagId", description = "Tag id", example = "1", required = true) Long tagId){
        return ResponseEntity.ok(productService.getProductsByTagId(tagId));
    }

    @PatchMapping
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

    public String getReadableCategory(String category){
        return switch (category.toLowerCase()) {
            case "cpu" -> "Procesoare";
            case "gpu" -> "Plăci video";
            case "mdb" -> "Plăci de baza";
            case "psu" -> "Surse";
            case "ram" -> "Memorii RAM";
            case "ssd" -> "SSD-uri";
            default -> "Alte produse";
        };
    }
}
