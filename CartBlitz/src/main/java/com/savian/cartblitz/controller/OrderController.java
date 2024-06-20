package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.OrderDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.mapper.OrderMapper;
import com.savian.cartblitz.model.Order;
import com.savian.cartblitz.model.OrderStatusEnum;
import com.savian.cartblitz.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;


@Slf4j
@Controller
@Validated
@RequestMapping("order")
@Tag(name = "Orders",description = "Endpoint manage Orders")
public class OrderController {
    OrderService orderService;
    OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orders including all fields",
            summary = "Showing all orders",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<OrderDto>> GetAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping(path = "/id/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a order with given id",
            summary = "Showing order with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Optional<OrderDto>> GetOrderById(
            @PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId){
        Optional<OrderDto> optionalOrder = orderService.getOrderById(orderId);

        if (optionalOrder.isPresent()) {
            return ResponseEntity.ok(optionalOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/id/{orderId}")
    @Operation(description = "Showing all info about a order with given id",
            summary = "Showing order with given id")
    @ApiResponses(value = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Access denied", responseCode = "403"),
            @ApiResponse(description = "Not Found", responseCode = "404")
    })
    public String viewOrder(@PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId,
                            Model model){
        if (orderId == null) {
            return "redirect:/";
        }
        Optional<OrderDto> orderOptional = orderService.getOrderById(orderId);

        if(orderOptional.isEmpty()){
            return "redirect:/";
        }
        Order order = orderMapper.orderDtoToOrder(orderOptional.get());

        model.addAttribute("order", order);

        return "orderDetails";
    }

    @GetMapping(path = "/customer/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orders written by the customer with the given id",
            summary = "Showing orders from the given customer",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<OrderDto>> GetOrdersByCustomerId(
            @PathVariable @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId){
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @GetMapping(path = "/status/{status}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about orders with the given status",
            summary = "Showing orders with the given status",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<OrderDto>> GetOrdersByStatus(
            @PathVariable @Parameter(name = "status", description = "Status", required = true) OrderStatusEnum status){
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PostMapping(path = "/complete/{orderId}")
    @Operation(description = "Complete an order with the given ID and redirect to the home page",
            summary = "Complete order with given ID",
            responses = {
                    @ApiResponse(description = "Order completed successfully", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Order not found", responseCode = "404")
            })
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build();
    }

    @GetMapping(path = "/modify{amount}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Modify the total amount for the order with the given id",
            summary = "Modify total amount for order with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<OrderDto> ModifyTotalAmount(
            @RequestParam Long orderId,
            @Valid @Digits(integer = 8, fraction = 2, message = "Total amount must have up to 8 digits before and 2 digits after the decimal point")
            @NotNull @PathVariable @RequestParam BigDecimal amount){
        return ResponseEntity.ok(orderService.modifyTotalAmount(orderId, amount));
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating order for given customer id",
            summary = "Creating a new order",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<OrderDto> CreateOrder(
            @Valid @RequestParam Long customerId){
        OrderDto order = orderService.saveOrder(customerId);
        return ResponseEntity.created(URI.create("/order/" + order.getOrderId())).body(order);
    }

    @PutMapping(path = "/id/{orderId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a order with the given id",
            summary = "Updating order with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error",responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Order Not Found", responseCode = "404")
            })
    public ResponseEntity<OrderDto> UpdateOrder(@PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId,
                                                @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId){
        return ResponseEntity.ok(orderService.updateOrder(orderId, customerId));
    }

    @DeleteMapping(path = "/id/{orderId}")
    @Operation(description = "Deleting a order with a given id",
            summary = "Deleting a order with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Void> deleteOrder(@PathVariable @Parameter(name = "orderId", description = "Order id", example = "1", required = true) Long orderId) {
        try {
            orderService.removeOrderById(orderId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
