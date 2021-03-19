package com.jm.online_store.controller.simple;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/search")
public class SearchController {

    @GetMapping("/{searchString}")
    public String openSearchPage() {
        return "search";
    }
}
