package com.jm.online_store.controller.simple;

import com.jm.online_store.model.News;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping(value = "/news", method = RequestMethod.GET)
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public String newsPage(Model model) {

        List<News> newsList = newsService.getAllPublished(LocalDate.now());
        model.addAttribute("news", newsList);
        return "newsPage";
    }

    @GetMapping("/{id}")
    public String newsDetails(@PathVariable(value = "id") Long id, Model model) {

        if (!newsService.existsById(id)) {
            return "redirect:/news";
        }
        News news = newsService.findById(id);
        model.addAttribute("news", news);
        return "newsDetails";
    }
}
