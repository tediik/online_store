package com.jm.online_store.controller.rest;

import com.jm.online_store.model.News;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "api/manager", method = RequestMethod.GET)
public class ManagerRestController {

    private final NewsService newsService;

    @GetMapping
    public String managerPage(){
        return "managerPage";
    }

    /*@GetMapping("/news")
    public String newsManagement(Model model) {

        List<News> newsList = newsService.findAll();

        List<News> publishedNewsList = newsService
                .findAllByPostingDateBefore(LocalDateTime.now());

        List<News> unpublishedNewsList = newsService
                .findAllByPostingDateAfter(LocalDateTime.now());

        model.addAttribute("news", newsList);
        model.addAttribute("publishedNews", publishedNewsList);
        model.addAttribute("unpublishedNews", unpublishedNewsList);
        return "newsManagement";
    }*/

    @GetMapping("/news/add")
    public String newsAdd(Model model) {

        model.addAttribute("news", new News());
        return "newsAdd";
    }

    @PostMapping("/news/post")
    public String newsPost(News news) {

        if(news.getPostingDate() == null || news.getPostingDate().isBefore(LocalDateTime.now())) {
            news.setPostingDate(LocalDateTime.now().withSecond(0).withNano(0));
        }

        newsService.save(news);
        return "redirect:/manager/news";
    }

    @GetMapping("/news/{id}/edit")
    public String newsDetails(@PathVariable(value = "id") Long id, Model model) {

        if (!newsService.existsById(id)) {
            return "redirect:/manager/news";
        }

        News news = newsService.findById(id).orElseGet(News::new);
        model.addAttribute("news", news);
        return "newsEdit";
    }

    @PostMapping("/news/update")
    public String newsUpdate(News news) {

        if(news.getPostingDate() == null || news.getPostingDate().isBefore(LocalDateTime.now())) {
            news.setPostingDate(LocalDateTime.now().withSecond(0).withNano(0));
        }

        newsService.save(news);
        return "redirect:/manager/news";
    }

    @DeleteMapping("/news/{id}/delete")
    public String newsDelete(@PathVariable Long id) {

        newsService.deleteById(id);
        return "redirect:/manager/news";
    }
}
