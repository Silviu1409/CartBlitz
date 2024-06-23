package com.savian.cartblitz.controller;

import com.savian.cartblitz.dto.TagDto;
import com.savian.cartblitz.exception.ResourceNotFoundException;
import com.savian.cartblitz.service.TagService;
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
@RequestMapping("tag")
@Tag(name = "Tags",description = "Endpoint manage Tags")
public class TagController {
    TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about tags including all fields",
            summary = "Showing all tags",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<TagDto>>> GetAllTags() {
        List<TagDto> tagDtos = tagService.getAllTags();

        List<EntityModel<TagDto>> tagModels = tagDtos.stream()
                .map(tagDto -> EntityModel.of(tagDto,
                        linkTo(methodOn(TagController.class).GetTagById(tagDto.getTagId())).withSelfRel()))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(TagController.class).GetAllTags()).withSelfRel();
        CollectionModel<EntityModel<TagDto>> model = CollectionModel.of(tagModels, selfLink);

        return ResponseEntity.ok(model);
    }

    @GetMapping(path = "/id/{tagId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a tag with given id",
            summary = "Showing tag with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<TagDto>> GetTagById(
            @PathVariable
            @Parameter(name = "tagId", description = "Tag id", example = "1", required = true) Long tagId) {
        Optional<TagDto> optionalTag = tagService.getTagById(tagId);

        return optionalTag.map(tagDto ->
                        ResponseEntity.ok(EntityModel.of(tagDto,
                                linkTo(methodOn(TagController.class).GetTagById(tagId)).withSelfRel())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/name/{tagName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a tag with given name",
            summary = "Showing tag with given name",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<TagDto>> GetTagByName(
            @PathVariable
            @Parameter(name = "tagName", description = "Tag name", example = "Tag name", required = true) String tagName) {
        Optional<TagDto> optionalTag = tagService.getTagByName(tagName);

        return optionalTag.map(tagDto ->
                        ResponseEntity.ok(EntityModel.of(tagDto,
                                linkTo(methodOn(TagController.class).GetTagByName(tagName)).withSelfRel())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/product/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about tags for the product with the given id",
            summary = "Showing tags with the given product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<CollectionModel<EntityModel<TagDto>>> GetTagsByProductId(
            @PathVariable
            @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId) {
        List<TagDto> tagDtos = tagService.getTagsByProductId(productId);

        List<EntityModel<TagDto>> tagModels = tagDtos.stream()
                .map(tagDto -> EntityModel.of(tagDto,
                        linkTo(methodOn(TagController.class).GetTagById(tagDto.getTagId())).withSelfRel()))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(TagController.class).GetTagsByProductId(productId)).withSelfRel();
        CollectionModel<EntityModel<TagDto>> model = CollectionModel.of(tagModels, selfLink);

        return ResponseEntity.ok(model);
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating tag - all info will be put in",
            summary = "Creating a new tag",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<EntityModel<TagDto>> CreateTag(
            @Valid @RequestBody TagDto tagDto) {
        TagDto createdTag = tagService.saveTag(tagDto);

        return ResponseEntity.created(URI.create("/tag/" + createdTag.getTagId()))
                .body(EntityModel.of(createdTag,
                        linkTo(methodOn(TagController.class).GetTagById(createdTag.getTagId())).withSelfRel()));
    }

    @PutMapping(path = "/id/{tagId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updating the details of a tag with the given id",
            summary = "Updating tag with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Tag Not Found", responseCode = "404")
            })
    public ResponseEntity<EntityModel<TagDto>> UpdateTag(
            @PathVariable
            @Parameter(name = "tagId", description = "Tag id", example = "1", required = true) Long tagId,
            @Valid @RequestBody TagDto tagDto) {
        TagDto updatedTag = tagService.updateTag(tagId, tagDto);

        return ResponseEntity.ok(EntityModel.of(updatedTag,
                linkTo(methodOn(TagController.class).GetTagById(tagId)).withSelfRel()));
    }

    @DeleteMapping(path = "/id/{tagId}")
    @Operation(description = "Deleting a tag with a given id",
            summary = "Deleting a tag with a given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Tag Not Found", responseCode = "404")
            })
    public ResponseEntity<Void> DeleteTag(@PathVariable @Parameter(name = "tagId",description = "Tag id",example = "1",required = true) Long tagId) {
        try {
            tagService.removeTagById(tagId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
