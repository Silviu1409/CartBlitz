package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.model.OrderProduct;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.service.OrderProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Slf4j
@Controller
@Validated
@RequestMapping("orderProduct")
@Tag(name = "OrderProducts",description = "Endpoint manage OrderProducts")
public class OrderProductController {
    OrderProductService orderProductService;

    public OrderProductController(OrderProductService orderProductService) {
        this.orderProductService = orderProductService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @CircuitBreaker(name = "orderProductService", fallbackMethod = "fallbackForGetAllOrderProducts")
    @Operation(description = "Showing all info about orderProducts including all fields",
            summary = "Showing all orderProducts",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<OrderProduct>> GetAllOrderProducts() {
        List<OrderProduct> orderProducts = orderProductService.getAllOrderProducts();

        orderProducts.forEach(this::addLinks);

        return ResponseEntity.ok(CollectionModel.of(orderProducts,
                linkTo(methodOn(OrderProductController.class).GetAllOrderProducts()).withSelfRel()));
    }

    @GetMapping(path = "/orderId/{orderId}/productId/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @CircuitBreaker(name = "orderProductService", fallbackMethod = "fallbackForGetOrderProductById")
    @Operation(description = "Showing all info about a orderProduct with given id",
            summary = "Showing orderProduct with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<OrderProduct>> GetOrderProductById(
            @PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId,
            @PathVariable @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId) {

        Optional<OrderProduct> optionalOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(orderId, productId);

        if (optionalOrderProduct.isPresent()) {
            OrderProduct orderProduct = optionalOrderProduct.get();
            addLinks(orderProduct);
            return ResponseEntity.ok(EntityModel.of(orderProduct));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/order/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orderProducts written by the order with the given id",
            summary = "Showing orderProducts from the given order",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<OrderProduct>>> GetOrderProductsByOrderId(
            @PathVariable @Parameter(name = "orderId", description = "Order ID", example = "1", required = true) Long orderId) {
        List<OrderProduct> orderProducts = orderProductService.getOrderProductsByOrderId(orderId);
        orderProducts.forEach(this::addLinks);

        List<EntityModel<OrderProduct>> orderProductModels = orderProducts.stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(orderProductModels));
    }

    @GetMapping(path = "/product/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orderProducts for the product with the given id",
            summary = "Showing orderProducts with the given product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<OrderProduct>>> GetOrderProductsByProductId(
            @PathVariable @Parameter(name = "productId", description = "Product ID", example = "1", required = true) Long productId) {
        List<OrderProduct> orderProducts = orderProductService.getOrderProductsByProductId(productId);
        orderProducts.forEach(this::addLinks);

        List<EntityModel<OrderProduct>> orderProductModels = orderProducts.stream()
                .map(EntityModel::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(orderProductModels));
    }

    @GetMapping(path = "/{quantity}/orderId/{orderId}/productId/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Update the quantity of an order product",
            summary = "Update order product quantity")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Not Found", responseCode = "404")
    })
    public ResponseEntity<Void> UpdateOrderProductQuantity(
                                @PathVariable Integer quantity,
                                @PathVariable Long orderId,
                                @PathVariable Long productId,
                                RedirectAttributes redirectAttributes) {

        if (quantity <= 0) {
            orderProductService.removeOrderProductById(orderId, productId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/cart"))
                    .build();
        }

        Optional<OrderProduct> existingOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(orderId, productId);

        if(existingOrderProduct.isPresent()){
            Product product = existingOrderProduct.get().getProduct();

            if (quantity > product.getStockQuantity()) {
                redirectAttributes.addFlashAttribute("errorProductQuantity", "Nu sunt suficiente produse de tipul '" + product.getName() + "' în stoc.");
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create("/cart"))
                        .build();
            }
        }

        existingOrderProduct.ifPresent(orderProduct -> orderProductService.updateOrderProduct(orderId, productId, new OrderProductDto(orderId, productId, quantity, orderProduct.getPrice())));

        Optional<OrderProduct> updatedOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(orderId, productId);
        if (updatedOrderProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/cart"))
                    .build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating orderProduct - all info will be put in",
            summary = "Creating a new orderProduct",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<EntityModel<OrderProduct>> CreateOrderProduct(
            @Valid @RequestBody OrderProductDto orderProductDto) {
        OrderProduct orderProduct = orderProductService.saveOrderProduct(orderProductDto);
        addLinks(orderProduct);
        return ResponseEntity.created(URI.create("/orderProduct/orderId/" + orderProductDto.getOrderId() + "/productId/" + orderProductDto.getProductId()))
                .body(EntityModel.of(orderProduct));
    }

    @PutMapping(path = "/orderId/{orderId}/productId/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a orderProduct with the given id",
            summary = "Updating orderProduct with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "OrderProduct Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<OrderProduct>> UpdateOrderProduct(
                    @PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId,
                    @PathVariable @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId,
                    @Valid @RequestBody OrderProductDto orderProductDto) {
        OrderProduct updatedOrderProduct = orderProductService.updateOrderProduct(orderId, productId, orderProductDto);
        addLinks(updatedOrderProduct);
        return ResponseEntity.ok(EntityModel.of(updatedOrderProduct));
    }

    @DeleteMapping(path = "/orderId/{orderId}/productId/{productId}")
    @Operation(description = "Deleting a orderProduct with a given id",
            summary = "Deleting a orderProduct with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "OrderProduct Not Found", responseCode = "404"),
            })
    public ResponseEntity<Void> DeleteOrderProduct(@PathVariable @Parameter(name = "orderId",description = "Order id",example = "1",required = true) Long orderId,
                                                   @PathVariable @Parameter(name = "productId",description = "Product id",example = "1",required = true) Long productId) {
        try {
            orderProductService.removeOrderProductById(orderId, productId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private void addLinks(OrderProduct orderProduct) {
        orderProduct.add(linkTo(methodOn(OrderProductController.class).GetOrderProductById(orderProduct.getOrder().getOrderId(), orderProduct.getProduct().getProductId())).withSelfRel());
        orderProduct.add(linkTo(methodOn(OrderProductController.class).GetAllOrderProducts()).withRel("orderProducts"));
    }

    public ResponseEntity<CollectionModel<EntityModel<OrderProductDto>>> fallbackForGetAllOrderProducts(Exception ex) {
        log.error("Fallback method executed for GetAllOrderProducts due to {}", ex.toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    public ResponseEntity<EntityModel<OrderProductDto>> fallbackForGetOrderProductById(Long orderProductId, Exception ex) {
        log.error("Fallback method executed for GetOrderProductById with orderProductId {} due to {}", orderProductId, ex.toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
