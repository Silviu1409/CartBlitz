package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.*;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.mapper.ProductMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Slf4j
@Controller
@Validated
@RequestMapping("product")
@Tag(name = "Products",description = "Endpoint manage Products")
public class ProductController {
    ProductService productService;
    CustomerService customerService;
    OrderService orderService;
    OrderProductRepository orderProductRepository;
    OrderProductService orderProductService;
    ProductMapper productMapper;

    public ProductController(ProductService productService, CustomerService customerService, OrderService orderService, OrderProductRepository orderProductRepository, OrderProductService orderProductService, ProductMapper productMapper) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.orderProductRepository = orderProductRepository;
        this.orderProductService = orderProductService;
        this.productMapper = productMapper;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products including all fields",
            summary = "Showing all products")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> GetAllProducts() {
        List<ProductDto> productDtos = productService.getAllProducts();

        List<EntityModel<ProductDto>> productModels = productDtos.stream()
                .map(productDto -> {
                    Link selfLink = linkTo(ProductController.class).slash("id").slash(productDto.getProductId()).withSelfRel();
                    Link categoryLink = linkTo(ProductController.class).slash("category").slash(productDto.getCategory()).withRel("category");
                    return EntityModel.of(productDto, selfLink, categoryLink);
                })
                .collect(Collectors.toList());

        Link selfLink = linkTo(ProductController.class).withSelfRel();
        CollectionModel<EntityModel<ProductDto>> model = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(model);
    }

    @GetMapping(path = "/api/id/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Showing all info about a product with given id",
            summary = "Showing product with given id")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<EntityModel<ProductDto>> getProductByIdApi(
            @PathVariable Long productId) {
        Optional<Product> optionalProduct = productService.getProductById(productId);

        if (optionalProduct.isPresent()) {
            ProductDto productDto = productMapper.productToProductDto(optionalProduct.get());

            EntityModel<ProductDto> model = EntityModel.of(productDto);
            model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(productId)).withSelfRel());
            model.add(linkTo(methodOn(ProductController.class).getProductsByCategoryApi(productDto.getCategory())).withRel("productsInCategory"));

            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.notFound().build();
        }
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
                Optional<CustomerDto> optionalCustomer = customerService.getCustomerByUsername(username);

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

    @GetMapping(path = "/api/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Showing all info about products from the given category",
            summary = "Showing products with from the given category")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> getProductsByCategoryApi(
            @PathVariable String category) {
        List<ProductDto> products = productService.getProductsByCategory(category);
        Map<Long, Integer> numImagesMap = new HashMap<>();

        for (ProductDto productDto : products) {
            int numImages = productService.getNumImagesForProduct(productDto.getCategory().toLowerCase(), productDto.getProductId());
            numImagesMap.put(productDto.getProductId(), numImages);
        }

        List<EntityModel<ProductDto>> productModels = new ArrayList<>();
        for (ProductDto product : products) {
            Long productId = product.getProductId();
            EntityModel<ProductDto> model = EntityModel.of(product);
            model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(productId)).withSelfRel());
            productModels.add(model);
        }

        Link selfLink = linkTo(methodOn(ProductController.class).getProductsByCategoryApi(category)).withSelfRel();

        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/category/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = "/api/category/{category}/sort", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Sort products from the given category")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> sortProductsByCategoryApi(
            @PathVariable String category,
            @RequestParam String sortBy,
            @RequestParam String sortOrder) {
        List<ProductDto> products = productService.getProductsByCategory(category);
        List<ProductDto> sortedProducts = productService.sortProducts(products, sortBy, sortOrder);

        List<EntityModel<ProductDto>> productModels = sortedProducts.stream()
                .map(product -> {
                    EntityModel<ProductDto> model = EntityModel.of(product);
                    model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
                    model.add(linkTo(methodOn(ProductController.class).sortProductsByCategoryApi(category, sortBy, sortOrder)).withRel("sorted-products"));
                    return model;
                }).toList();

        Link selfLink = linkTo(methodOn(ProductController.class).sortProductsByCategoryApi(category, sortBy, sortOrder)).withSelfRel();
        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/category/{category}/sort", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = "/api/category/{category}/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Filter products from the given category by min price and max price")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> filterProductsByCategoryApi(
            @PathVariable String category,
            @RequestParam String minPrice,
            @RequestParam String maxPrice) {
        List<ProductDto> products = productService.getProductsByCategory(category);

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

        List<ProductDto> filteredProducts = productService.filterProductsMinPriceMaxPrice(products, minPriceValue, maxPriceValue);

        List<EntityModel<ProductDto>> productModels = filteredProducts.stream()
                .map(product -> {
                    EntityModel<ProductDto> model = EntityModel.of(product);
                    model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
                    model.add(linkTo(methodOn(ProductController.class).filterProductsByCategoryApi(category, minPrice, maxPrice)).withRel("filtered-products"));
                    return model;
                }).toList();

        Link selfLink = linkTo(methodOn(ProductController.class).filterProductsByCategoryApi(category, minPrice, maxPrice)).withSelfRel();
        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/category/{category}/filter", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search products", description = "Search for products based on the provided search query")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404")
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> searchProductsApi(
            @RequestParam(name = "search") String searchQuery) {
        List<ProductDto> products = productService.searchProducts(searchQuery);

        List<EntityModel<ProductDto>> productModels = products.stream()
                .map(product -> {
                    EntityModel<ProductDto> model = EntityModel.of(product);
                    model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
                    model.add(linkTo(methodOn(ProductController.class).searchProductsApi(searchQuery)).withRel("search-results"));
                    return model;
                }).toList();

        Link selfLink = linkTo(methodOn(ProductController.class).searchProductsApi(searchQuery)).withSelfRel();
        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = "/api/sort", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Sort products")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> sortProductsApi(
            @RequestParam String sortBy,
            @RequestParam String sortOrder) {
        List<ProductDto> products = productService.getAllProducts();
        List<ProductDto> sortedProducts = productService.sortProducts(products, sortBy, sortOrder);

        List<EntityModel<ProductDto>> productModels = sortedProducts.stream()
                .map(product -> {
                    EntityModel<ProductDto> model = EntityModel.of(product);
                    model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
                    model.add(linkTo(methodOn(ProductController.class).sortProductsApi(sortBy, sortOrder)).withRel("sorted-products"));
                    return model;
                }).toList();

        Link selfLink = linkTo(methodOn(ProductController.class).sortProductsApi(sortBy, sortOrder)).withSelfRel();
        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/sort", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(path = "/api/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Filter products by min price and max price",
            summary = "Filter products based on min and max price")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> filterProductsApi(
            @RequestParam String minPrice,
            @RequestParam String maxPrice) {
        List<ProductDto> products = productService.getAllProducts();

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

        List<ProductDto> filteredProducts = productService.filterProductsMinPriceMaxPrice(products, minPriceValue, maxPriceValue);

        List<EntityModel<ProductDto>> productModels = filteredProducts.stream()
                .map(product -> {
                    EntityModel<ProductDto> model = EntityModel.of(product);
                    model.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
                    model.add(linkTo(methodOn(ProductController.class).filterProductsApi(minPrice, maxPrice)).withRel("filtered-products"));
                    return model;
                }).toList();

        Link selfLink = linkTo(methodOn(ProductController.class).filterProductsApi(minPrice, maxPrice)).withSelfRel();
        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Filter products by min price and max price",
            summary = "Filter products based on min and max price")
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
    @Operation(summary = "Add a product to the shopping cart",
            description = "Adds a product identified by its ID to the authenticated customer's shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Product added to cart, redirecting to cart page"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Customer or Product not found")
    })
    public ResponseEntity<String> addToCart(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        Optional<CustomerDto> customer = customerService.getCustomerByUsername(username);

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
            summary = "Showing products with from the given brand")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> GetProductsByBrand(
            @PathVariable @Parameter(name = "brand", description = "Product brand", example = "Intel", required = true) String brand) {
        List<ProductDto> products = productService.getProductsByBrand(brand);
        List<EntityModel<ProductDto>> productModels = new ArrayList<>();

        for (ProductDto product : products) {
            EntityModel<ProductDto> productModel = EntityModel.of(product);
            productModel.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
            productModel.add(linkTo(methodOn(ProductController.class).GetProductsByBrand(brand)).withRel("products-by-brand"));
            productModels.add(productModel);
        }

        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels);
        collectionModel.add(linkTo(methodOn(ProductController.class).GetProductsByBrand(brand)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/priceRange", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products in the given price range",
            summary = "Showing products with from the given price range")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not Found", responseCode = "404"),
    })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> GetProductsByPriceRange(
            @RequestParam(name = "minPrice", required = false, defaultValue = "0.00") BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "100000000.00") BigDecimal maxPrice) {
        List<ProductDto> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        List<EntityModel<ProductDto>> productModels = new ArrayList<>();

        for (ProductDto product : products) {
            EntityModel<ProductDto> productModel = EntityModel.of(product);
            productModel.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
            productModel.add(linkTo(methodOn(ProductController.class).GetProductsByPriceRange(minPrice, maxPrice)).withRel("products-by-price-range"));
            productModels.add(productModel);
        }

        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels);
        collectionModel.add(linkTo(methodOn(ProductController.class).GetProductsByPriceRange(minPrice, maxPrice)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/tag/{tagId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about products with the given tag id",
            summary = "Showing products from the given tag id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
            })
    public ResponseEntity<CollectionModel<EntityModel<ProductDto>>> GetProductsByTagId(
            @PathVariable
            @Parameter(name = "tagId", description = "Tag id", example = "1", required = true) Long tagId) {
        List<ProductDto> products = productService.getProductsByTagId(tagId);
        List<EntityModel<ProductDto>> productModels = new ArrayList<>();

        for (ProductDto product : products) {
            EntityModel<ProductDto> productModel = EntityModel.of(product);
            productModel.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
            productModel.add(linkTo(methodOn(ProductController.class).GetProductsByTagId(tagId)).withRel("products-by-tag"));
            productModels.add(productModel);
        }

        CollectionModel<EntityModel<ProductDto>> collectionModel = CollectionModel.of(productModels);
        collectionModel.add(linkTo(methodOn(ProductController.class).GetProductsByTagId(tagId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PatchMapping
    @Operation(description = "Update the stock quantity for a given product",
            summary = "Update stock quantity",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<Product>> UpdateStockQuantity(
            @RequestParam Long productId,
            @RequestParam Integer stockQuantity) {
        Product product = productService.updateStockQuantity(productId, stockQuantity);
        EntityModel<Product> productModel = EntityModel.of(product);
        productModel.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());

        return ResponseEntity.ok(productModel);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating product - all info will be put in",
            summary = "Creating a new product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<EntityModel<Product>> CreateProduct(
            @Valid @RequestBody ProductDto productDto) {
        Product product = productService.saveProduct(productDto);
        EntityModel<Product> productModel = EntityModel.of(product);
        productModel.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());
        productModel.add(linkTo(methodOn(ProductController.class).CreateProduct(productDto)).withRel("create-product"));

        return ResponseEntity.created(URI.create("/product/" + product.getProductId())).body(productModel);
    }

    @PutMapping(path = "/id/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a product with the given id",
            summary = "Updating product with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Product Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<Product>> UpdateProduct(@PathVariable @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId,
                                                              @Valid @RequestBody ProductDto productDto) {
        Product product = productService.updateProduct(productId, productDto);
        EntityModel<Product> productModel = EntityModel.of(product);
        productModel.add(linkTo(methodOn(ProductController.class).getProductByIdApi(product.getProductId())).withSelfRel());

        return ResponseEntity.ok(productModel);
    }

    @DeleteMapping(path = "/id/{productId}")
    @Operation(description = "Deleting a product with a given id",
            summary = "Deleting a product with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Product Not Found", responseCode = "404")
            })
    public ResponseEntity<Void> DeleteProduct(@PathVariable @Parameter(name = "productId",description = "Product id",example = "1",required = true) Long productId) {
        try {
            productService.removeProductById(productId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
