package com.jm.online_store.controller.simple;

import com.jm.online_store.model.News;
import com.jm.online_store.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/manager", method = RequestMethod.GET)
public class ManagerController {

    @Autowired
    NewsService newsService;

    @GetMapping
    public String managerPage(){
        return "managerPage";
    }

    @GetMapping("/news")
    public String newsManagement(Model model) {

        model.addAttribute("news", newsService.findAll());
        return "newsManagement";
    }

    @GetMapping("/news/add")
    public String newsAdd(Model model) {

        model.addAttribute("news", new News());
        return "newsAdd";
    }

    @PostMapping("/news/post")
    public String newsPost(News news, Model model) {

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
    public String newsUpdate(News news, Model model) {

        newsService.save(news);
        return "redirect:/manager/news";
    }

    @DeleteMapping("/news/{id}/delete")
    public String newsDelete(@PathVariable Long id) {

        newsService.deleteById(id);
        return "redirect:/manager/news";
    }
}
