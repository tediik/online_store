package com.jm.online_store.controller.rest;

import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/news", method = RequestMethod.GET)
public class NewsRestController {

    private final NewsService newsService;

    @GetMapping("/all")
    public ResponseEntity<List<News>> newsPage() {
        return new ResponseEntity<>(newsService.getAllPublished(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> newsDetails(@PathVariable Long id) {
        News news = newsService.findById(id);
        return ResponseEntity.ok(news);
    }
}
