package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@Validated
@RequestMapping("customer")
@Tag(name = "Customers", description = "Endpoint manage Customers")
public class CustomerController {
    CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about customers including all fields",
            summary = "Showing all customers",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<CustomerDto>> GetAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping(path = "/id/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a customer with given id",
            summary = "Showing customer with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Optional<Customer>> getCustomerById(
            @PathVariable
            @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId){
                Optional<Customer> optionalCustomer = customerService.getCustomerById(customerId);

                if (optionalCustomer.isPresent()) {
                    return ResponseEntity.ok(optionalCustomer);
                } else {
                    return ResponseEntity.notFound().build();
                }
    }

    @GetMapping(path = "/username/{username}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a customer with given username",
            summary = "Showing customer with given username",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Optional<Customer>> getCustomerByUsername(
            @PathVariable
            @Parameter(name = "username", description = "Customer username", example = "username", required = true) String username){
        return ResponseEntity.ok(customerService.getCustomerByUsername(username));
    }

    @GetMapping(path = "/asc",produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about Customers in ascending order of their full names.",
            summary = "Showing customers alphabetically",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<CustomerDto>> GetCustomersAscFullName(){
        return ResponseEntity.ok(customerService.getCustomersAscFullName());
    }

    @GetMapping(path = "/desc",produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about Customers in descending order of their full names.",
            summary = "Showing customers in reverse alphabetical order",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<CustomerDto>> GetCustomersDescFullName(){
        return ResponseEntity.ok(customerService.getCustomersDescFullName());
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating customer - all info will be put in",
            summary = "Creating a new customer",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<Customer> CreateCustomer(
                @Valid @RequestBody CustomerDto customerDto){
            Customer customer = customerService.saveCustomer(customerDto);
            return ResponseEntity.created(URI.create("/customer/" + customer.getCustomerId())).body(customer);
    }

    @PutMapping(path = "/id/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a customer with the given id",
            summary = "Updating customer with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Customer> UpdateCustomer(@PathVariable @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId,
                                                   @Valid @RequestBody CustomerDto customerDto){
        return  ResponseEntity.ok(customerService.updateCustomer(customerId, customerDto));
    }

    @DeleteMapping(path = "/id/{customerId}")
    @Operation(description = "Deleting a customer with a given id",
            summary = "Deleting a customer with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public void DeleteCustomer(@PathVariable @Parameter(name = "customerId",description = "Customer id",example = "1",required = true) Long customerId) {
        customerService.removeCustomerById(customerId);
    }
}
