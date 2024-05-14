package com.savian.cartblitz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CategoryController {
    @GetMapping("/categories")
    public String showCategories(Model model) {
        Map<String, String> categories = getCategoryMap();

        model.addAttribute("categories", categories);

        return "categories";
    }

    private Map<String, String> getCategoryMap() {
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
