package com.jm.online_store.controller.rest;

import com.jm.online_store.model.News;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@Api("Rest controller for news")
@RequestMapping(value = "/api/news", method = RequestMethod.GET)
public class NewsRestController {

    private final NewsService newsService;

    @GetMapping("/all")
    @ApiOperation(value = "Get all news",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<News>> newsPageRest() {
        List<News> news = newsService.getAllPublished();
        return ResponseEntity.ok(news);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get full text by id",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> newsDetails(@PathVariable Long id) {
        News newsFound = newsService.findById(id);
        String newsff = newsFound.getFullText();
        return ResponseEntity.ok(newsff);
    }
}
