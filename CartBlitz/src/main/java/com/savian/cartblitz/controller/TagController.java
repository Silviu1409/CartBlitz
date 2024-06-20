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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;


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
    public ResponseEntity<List<TagDto>> GetAllTags(){
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping(path = "/id/{tagId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a tag with given id",
            summary = "Showing tag with given id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Optional<TagDto>> GetTagById(
            @PathVariable
            @Parameter(name = "tagId", description = "Tag id", example = "1", required = true) Long tagId){
        Optional<TagDto> optionalTag = tagService.getTagById(tagId);

        if (optionalTag.isPresent()) {
            return ResponseEntity.ok(optionalTag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/name/{tagName}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about a tag with given name",
            summary = "Showing tag with given name",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<Optional<TagDto>> GetTagByName(
            @PathVariable
            @Parameter(name = "tagName", description = "Tag name", example = "Tag name", required = true) String tagName){
        Optional<TagDto> optionalTag = tagService.getTagByName(tagName);

        if (optionalTag.isPresent()) {
            return ResponseEntity.ok(optionalTag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/product/{productId}", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Showing all info about tags for the product with the given id",
            summary = "Showing tags with the given product",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            })
    public ResponseEntity<List<TagDto>> GetTagsByProductId(
            @PathVariable
            @Parameter(name = "productId", description = "Product id", example = "1", required = true) Long productId){
        return ResponseEntity.ok(tagService.getTagsByProductId(productId));
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creating tag - all info will be put in",
            summary = "Creating a new tag",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "201"),
                    @ApiResponse(description = "Field validation error", responseCode = "400"),
                    @ApiResponse(description = "Access denied", responseCode = "403"),
                    @ApiResponse(description = "Bad Request - validation error per request", responseCode = "500")
            })
    public ResponseEntity<TagDto> CreateTag(
            @Valid @RequestBody TagDto tagDto){
        TagDto tag = tagService.saveTag(tagDto);
        return ResponseEntity.created(URI.create("/tag/" + tag.getTagId())).body(tag);
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
    public ResponseEntity<TagDto> UpdateTag(@PathVariable @Parameter(name = "tagId", description = "Tag id", example = "1", required = true) Long tagId,
                                            @Valid @RequestBody TagDto tagDto){
        return ResponseEntity.ok(tagService.updateTag(tagId, tagDto));
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
