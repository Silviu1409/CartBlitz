package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.WarrantyDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.service.WarrantyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Slf4j
@RestController
@Validated
@RequestMapping("warranty")
@Tag(name = "Warranties",description = "Endpoint manage Warranties")
public class WarrantyController {
    WarrantyService warrantyService;

    public WarrantyController(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about warranties including all fields",
            summary = "Showing all warranties",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<WarrantyDto>>> GetAllWarranties(){
        List<WarrantyDto> warrantyDtos = warrantyService.getAllWarranties();

        List<EntityModel<WarrantyDto>> warrantyModels = warrantyDtos.stream()
                .map(warrantyDto -> EntityModel.of(warrantyDto,
                        linkTo(methodOn(WarrantyController.class).GetWarrantyById(warrantyDto.getWarrantyId())).withSelfRel()))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(WarrantyController.class).GetAllWarranties()).withSelfRel();
        CollectionModel<EntityModel<WarrantyDto>> model = CollectionModel.of(warrantyModels, selfLink);

        return ResponseEntity.ok(model);
    }

    @GetMapping(path = "/id/{warrantyId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a warranty with given id",
            summary = "Showing warranty with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<WarrantyDto>> GetWarrantyById(
            @PathVariable
            @Parameter(name = "warrantyId", description = "Warranty id", example = "1", required = true) Long warrantyId){
        Optional<WarrantyDto> optionalWarranty = warrantyService.getWarrantyById(warrantyId);

        return optionalWarranty.map(warrantyDto ->
                        ResponseEntity.ok(EntityModel.of(warrantyDto,
                                linkTo(methodOn(WarrantyController.class).GetWarrantyById(warrantyId)).withSelfRel())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating warranty - all info will be put in",
            summary = "Creating a new warranty",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<EntityModel<WarrantyDto>> CreateWarranty(
            @Valid @RequestBody WarrantyDto warrantyDto){
        WarrantyDto createdWarranty = warrantyService.saveWarranty(warrantyDto);

        return ResponseEntity.created(URI.create("/warranty/" + createdWarranty.getWarrantyId()))
                .body(EntityModel.of(createdWarranty,
                        linkTo(methodOn(WarrantyController.class).GetWarrantyById(createdWarranty.getWarrantyId())).withSelfRel()));
    }

    @PutMapping(path = "/id/{warrantyId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a warranty with the given id",
            summary = "Updating warranty with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Warranty Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<WarrantyDto>> UpdateWarranty(
            @PathVariable
            @Parameter(name = "warrantyId", description = "Warranty id", example = "1", required = true) Long warrantyId,
            @Valid @RequestBody WarrantyDto warrantyDto) {
        WarrantyDto updatedWarranty = warrantyService.updateWarranty(warrantyId, warrantyDto);

        return ResponseEntity.ok(EntityModel.of(updatedWarranty,
                linkTo(methodOn(WarrantyController.class).GetWarrantyById(warrantyId)).withSelfRel()));
    }

    @DeleteMapping(path = "/id/{warrantyId}")
    @Operation(description = "Deleting a warranty with a given id",
            summary = "Deleting a warranty with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Warranty Not Found", responseCode = "404")
            })
    public ResponseEntity<Void> DeleteWarranty(@PathVariable @Parameter(name = "warrantyId",description = "Warranty id",example = "1",required = true) Long warrantyId) {
        try {
            warrantyService.removeWarrantyById(warrantyId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
