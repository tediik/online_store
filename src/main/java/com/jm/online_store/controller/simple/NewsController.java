package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AllArgsConstructor
@Controller
@RequestMapping(value = "/news", method = RequestMethod.GET)
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public String newsPage() {
        return "news-page";
    }

    @GetMapping("/{id}")
    public String newsDetails(@PathVariable(value = "id") Long id) {
        if (!newsService.existsById(id)) {
            return "redirect:/news";
        }
        return "news-details";
    }
}
