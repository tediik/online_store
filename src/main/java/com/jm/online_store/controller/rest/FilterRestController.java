package com.jm.online_store.controller.rest;

import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.filter.Filters;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.FilterService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.util.Transliteration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@AllArgsConstructor
@RestController
@RequestMapping("/api/filter")
public class FilterRestController {

    private final FilterService filterService;


    @GetMapping("/all/{category}")
    public Filters getFilters(@PathVariable String category) {
        return filterService.getFilters(category);
    }

    @GetMapping("/filtered/{category}")
    public List<ProductDto> filterProduct(@PathVariable String category,
                                         @RequestParam(required = false) List<Long> price,
                                         @RequestParam(required = false) List<String> brand,
                                         @RequestParam(required = false) List<String> color,
                                         @RequestParam(required = false) List<String> RAM,
                                         @RequestParam(required = false) List<String> storage,
                                         @RequestParam(required = false) List<String> screenResolution,
                                         @RequestParam(required = false) List<String> OS,
                                         @RequestParam(required = false) List<String> bluetooth) {

        return filterService.filterProducts(Transliteration.latinToCyrillic(category),
                price, brand, color, RAM, storage, screenResolution, OS, bluetooth);
    }
}