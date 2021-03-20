package com.jm.online_store.controller.rest;

import com.jm.online_store.model.filter.Filter;
import com.jm.online_store.model.filter.Filters;
import com.jm.online_store.service.interf.FilterService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@AllArgsConstructor
@RestController
@RequestMapping("/api/filter")
public class FilterRestController {

    private final ProductService productService;
    private final FilterService filterService;


    @GetMapping("/{category}")
    public Filters getFilters(@PathVariable String category) {
        if (category.equals("Smartfony")) {
            return filterService.getSmartphoneFilters(category);
        } else {
            return null;
        }
    }
}
