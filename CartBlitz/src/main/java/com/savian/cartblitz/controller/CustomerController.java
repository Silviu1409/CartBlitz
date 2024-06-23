package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.CustomerDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.model.Customer;
import com.savian.cartblitz.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
    public ResponseEntity<CollectionModel<EntityModel<CustomerDto>>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();

        List<EntityModel<CustomerDto>> customerModels = customers.stream()
                .map(customer -> {
                    EntityModel<CustomerDto> customerModel = EntityModel.of(customer);
                    customerModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomerById(customer.getCustomerId())).withSelfRel());
                    return customerModel;
                })
                .collect(Collectors.toList());

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getAllCustomers()).withSelfRel();

        CollectionModel<EntityModel<CustomerDto>> collectionModel = CollectionModel.of(customerModels, selfLink);

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(path = "/id/{customerId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a customer with given id",
            summary = "Showing customer with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<CustomerDto>> getCustomerById(
            @PathVariable
            @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId){
                Optional<CustomerDto> optionalCustomer = customerService.getCustomerById(customerId);

                if (optionalCustomer.isPresent()) {
                    CustomerDto customer = optionalCustomer.get();
                    EntityModel<CustomerDto> customerModel = EntityModel.of(customer);
                    customerModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomerById(customerId)).withSelfRel());
                    customerModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getAllCustomers()).withRel("all-customers"));

                    return ResponseEntity.ok(customerModel);
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
    public ResponseEntity<EntityModel<CustomerDto>> getCustomerByUsername(
            @PathVariable
            @Parameter(name = "username", description = "Customer username", example = "username", required = true) String username){
        Optional<CustomerDto> optionalCustomer = customerService.getCustomerByUsername(username);

        if (optionalCustomer.isPresent()) {
            CustomerDto customer = optionalCustomer.get();
            EntityModel<CustomerDto> customerModel = EntityModel.of(customer);
            customerModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomerByUsername(username)).withSelfRel());
            customerModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getAllCustomers()).withRel("all-customers"));

            return ResponseEntity.ok(customerModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/asc",produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about Customers in ascending order of their full names.",
            summary = "Showing customers alphabetically",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<CustomerDto>> getCustomersAscFullName() {
        List<CustomerDto> customers = customerService.getCustomersAscFullName();
        CollectionModel<CustomerDto> resource = CollectionModel.of(customers);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersAscFullName()).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersDescFullName()).withRel("desc"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping(path = "/desc",produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about Customers in descending order of their full names.",
            summary = "Showing customers in reverse alphabetical order",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<CustomerDto>> getCustomersDescFullName() {
        List<CustomerDto> customers = customerService.getCustomersDescFullName();
        CollectionModel<CustomerDto> resource = CollectionModel.of(customers);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersDescFullName()).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersAscFullName()).withRel("asc"));

        return ResponseEntity.ok(resource);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating customer - all info will be put in",
            summary = "Creating a new customer",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<EntityModel<Customer>> createCustomer(
            @Valid @RequestBody CustomerDto customerDto) {
        Customer customer = customerService.saveCustomer(customerDto);
        EntityModel<Customer> resource = EntityModel.of(customer);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).createCustomer(customerDto)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomerById(customer.getCustomerId())).withRel("customer"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersAscFullName()).withRel("customersAsc"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersDescFullName()).withRel("customersDesc"));

        return ResponseEntity.created(URI.create("/customer/" + customer.getCustomerId())).body(resource);
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
    public ResponseEntity<EntityModel<Customer>> updateCustomer(@PathVariable @Parameter(name = "customerId", description = "Customer id", example = "1", required = true) Long customerId,
                                                                @Valid @RequestBody CustomerDto customerDto){
        Customer updatedCustomer = customerService.updateCustomer(customerId, customerDto);
        EntityModel<Customer> resource = EntityModel.of(updatedCustomer);

        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).updateCustomer(customerId, customerDto)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomerById(customerId)).withRel("customer"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersAscFullName()).withRel("customersAsc"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomersDescFullName()).withRel("customersDesc"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping(path = "/id/{customerId}")
    @Operation(description = "Deleting a customer with a given id",
            summary = "Deleting a customer with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Void> deleteCustomer(@PathVariable @Parameter(name = "customerId",description = "Customer id",example = "1",required = true) Long customerId) {
        try {
            customerService.removeCustomerById(customerId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
