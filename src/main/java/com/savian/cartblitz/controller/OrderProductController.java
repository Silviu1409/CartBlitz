package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.OrderProductDto;
import com.savian.cartblitz.model.OrderProduct;
import com.savian.cartblitz.model.Product;
import com.savian.cartblitz.service.OrderProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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
    @Operation(description = "Showing all info about orderProducts including all fields",
            summary = "Showing all orderProducts",
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
    public ResponseEntity<List<OrderProduct>> GetAllOrderProducts(){
        return ResponseEntity.ok(orderProductService.getAllOrderProducts());
    }

    @GetMapping(path = "/orderId/{orderId}/productId/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a orderProduct with given id",
            summary = "Showing orderProduct with given id",
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
    public ResponseEntity<Optional<OrderProduct>> GetOrderProductById(
            @PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId,
            @PathVariable @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId){
        Optional<OrderProduct> optionalOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(orderId, productId);

        if (optionalOrderProduct.isPresent()) {
            return ResponseEntity.ok(optionalOrderProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/order/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orderProducts written by the order with the given id",
            summary = "Showing orderProducts from the given order",
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
    public ResponseEntity<List<OrderProduct>> GetOrderProductsByOrderId(
            @PathVariable
            @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId){
        return ResponseEntity.ok(orderProductService.getOrderProductsByOrderId(orderId));
    }

    @GetMapping(path = "/product/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orderProducts for the product with the given id",
            summary = "Showing orderProducts with the given product",
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
    public ResponseEntity<List<OrderProduct>> GetOrderProductsByProductId(
            @PathVariable
            @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId){
        return ResponseEntity.ok(orderProductService.getOrderProductsByProductId(productId));
    }

    @RequestMapping("/{quantity}/orderId/{orderId}/productId/{productId}")
    @Operation(description = "Update the quantity of an order product",
            summary = "Update order product quantity")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200" ),
            @ApiResponse(description = "OrderProduct Not Found", responseCode = "404"),
    })
    public String UpdateOrderProductQuantity(@PathVariable Integer quantity,
                                             @PathVariable Long orderId,
                                             @PathVariable Long productId,
                                             RedirectAttributes redirectAttributes) {
        if(quantity <= 0){
            return DeleteOrderProductByOrderIdProductId(orderId, productId);
        }

        Optional<OrderProduct> existingOrderProduct = orderProductService.getOrderProductByOrderIdAndProductId(orderId, productId);

        if(existingOrderProduct.isPresent()){
            Product product = existingOrderProduct.get().getProduct();

            if (quantity > product.getStockQuantity()) {
                redirectAttributes.addFlashAttribute("errorProductQuantity", "Nu sunt suficiente produse de tipul '" + product.getName() + "' în stoc.");

                return "redirect:/cart";
            }
        }

        existingOrderProduct.ifPresent(orderProduct -> orderProductService.updateOrderProduct(orderId, productId, new OrderProductDto(orderId, productId, quantity, orderProduct.getPrice())));

        return "redirect:/cart";
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating orderProduct - all info will be put in",
            summary = "Creating a new orderProduct",
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
    public ResponseEntity<OrderProduct> CreateOrderProduct(
            @Valid @RequestBody OrderProductDto orderProductDto){
        OrderProduct orderProduct = orderProductService.saveOrderProduct(orderProductDto);
        return ResponseEntity.created(URI.create("/orderProduct/orderId/" + orderProductDto.getOrderId() + "/productId/" + orderProductDto.getProductId())).body(orderProduct);
    }

    @PutMapping(path = "/orderId/{orderId}/productId/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a orderProduct with the given id",
            summary = "Updating orderProduct with given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "OrderProduct Not Found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Field validation error",
                            responseCode = "400"
                    ),
            })
    public ResponseEntity<OrderProduct> UpdateOrderProduct(@PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId,
                                                           @PathVariable @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId,
                                               @Valid @RequestBody OrderProductDto orderProductDto){
        return ResponseEntity.ok(orderProductService.updateOrderProduct(orderId, productId, orderProductDto));
    }

    @DeleteMapping(path = "/orderId/{orderId}/productId/{productId}")
    @Operation(description = "Deleting a orderProduct with a given id",
            summary = "Deleting a orderProduct with a given id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "OrderProduct Not Found",
                            responseCode = "404"
                    ),
            })
    public void DeleteOrderProduct(@PathVariable @Parameter(name = "orderId",description = "Order id",example = "1",required = true) Long orderId,
                                   @PathVariable @Parameter(name = "productId",description = "Product id",example = "1",required = true) Long productId) {
        orderProductService.removeOrderProductById(orderId, productId);
    }

    @RequestMapping(path = "/delete/orderId/{orderId}/productId/{productId}")
    @Operation(description = "Deleting a orderProduct with a given id",
            summary = "Deleting a orderProduct with a given id")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200" ),
            @ApiResponse(description = "OrderProduct Not Found", responseCode = "404"),
    })
    public String DeleteOrderProductByOrderIdProductId(@PathVariable Long orderId,
                                                       @PathVariable Long productId) {
        orderProductService.removeOrderProductById(orderId, productId);

        return "redirect:/cart";
    }
}
