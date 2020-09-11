package com.jm.online_store.controller.rest;

import com.jm.online_store.model.News;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Рест контроллер для управления новостями из кабинете менеджера, а также публикации новостей
 * на странице новостей
 */
@AllArgsConstructor
@RestController
@RequestMapping(value = "api/manager", method = RequestMethod.GET)
public class ManagerRestController {

    private final NewsService newsService;

    /**
     * Метод возвращающий страницу новостей
     *
     * @param page параметры страницы
     * @return Page<News> возвращает страницу новостей из базы данных
     */
    @GetMapping("/news")
    public ResponseEntity<Page<News>> allNews(@PageableDefault Pageable page) {
        Page<News> response = newsService.findAll(page);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * Метод сохраняет новости в базу данных
     *
     * @param news сущность для сохранения в базе данных
     * @return возвращает заполненную сущность клиенту
     */
    @PostMapping("/news/post")
    public ResponseEntity<News> newsPost(@RequestBody News news) {

        if (news.getPostingDate() == null || news.getPostingDate().isBefore(LocalDateTime.now())) {
            news.setPostingDate(LocalDateTime.now().withSecond(0).withNano(0));
        }

        newsService.save(news);
        return ResponseEntity.ok().body(news);
    }

    /**
     * Метод обновляет сущность в базе данных
     *
     * @param news сущность для сохранения в базе данных
     * @return возвращает обновленную сущность клиенту
     */
    @PutMapping("/news/update")
    public ResponseEntity<News> newsUpdate(@RequestBody News news) {

        if (news.getPostingDate() == null || news.getPostingDate().isBefore(LocalDateTime.now())) {
            news.setPostingDate(LocalDateTime.now().withSecond(0).withNano(0));
        }

        newsService.save(news);
        return ResponseEntity.ok().body(news);
    }

    /**
     * Метод удаляет сушность из базы данных по уникальному идентификатору
     *
     * @param id уникальный идентификатор
     * @return возвращает идентификатор удаленной сущности клиенту
     */
    @DeleteMapping("/news/{id}/delete")
    public ResponseEntity<Long> newsDelete(@PathVariable Long id) {

        newsService.deleteById(id);
        return ResponseEntity.ok().body(id);
    }
}
