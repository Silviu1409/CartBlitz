package com.savian.cartblitz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
@Validated
@Tag(name = "Product Categories", description = "Operations pertaining to product categories")
public class CategoryController {

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            description = "Showing all info about product categories",
            summary = "Showing all product categories",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
    public String showCategories(Model model) {
        Map<String, String> categories = getCategoryMap();

        model.addAttribute("categories", categories);

        return "categories";
    }

    public Map<String, String> getCategoryMap() {
        Map<String, String> categories = new HashMap<>();

        categories.put("CPU", "Procesoare");
        categories.put("GPU", "Plăci video");
        categories.put("MDB", "Plăci de baza");
        categories.put("PSU", "Surse");
        categories.put("RAM", "Memorii RAM");
        categories.put("SSD", "SSD-uri");

        return categories;
    }
}
