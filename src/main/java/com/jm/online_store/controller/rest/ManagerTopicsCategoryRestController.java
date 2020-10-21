package com.jm.online_store.controller.rest;

import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.service.interf.TopicsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RestController для чтения/добавления/изменения/архивирования категорий тем для обратной связи
 */
@RestController
@RequestMapping("/api/manager/topicsCategory")
@RequiredArgsConstructor
public class ManagerTopicsCategoryRestController {
    private final TopicsCategoryService topicsCategoryService;

    /**
     * Метод для получения всех категорий тем
     *
     * @return ResponseEntity<List <TopicsCategory>> возвращает все
     * категории тем со статусом ответа, если категорий тем нет - только статус
     */
    @GetMapping
    public ResponseEntity<List<TopicsCategory>> readAllTopicsCategories() {
        List<TopicsCategory> topicsCategories = topicsCategoryService.findAll();
        if (topicsCategories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(topicsCategories);
    }

    /**
     * Метод для получения единственной категории тем
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает единственную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicsCategory> readTopicsCategory(@PathVariable(name = "id") long id) {
        if (!topicsCategoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topicsCategoryService.findById(id));
    }

    /**
     * Метод для добавления новой категории тем
     *
     * @param topicsCategory категория тем, которая будет создана
     * @return ResponseEntity<TopicsCategory> возвращает созданную категорию тем со статусом ответа,
     * если категория тем с таким именем уже существует - только статус
     */
    @PostMapping
    public ResponseEntity<TopicsCategory> createTopicsCategory(@RequestBody TopicsCategory topicsCategory) {
        if (topicsCategoryService.existsByCategoryName(topicsCategory.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(topicsCategoryService.creat(topicsCategory));
    }

    /**
     * Методя для изменения категории тем
     *
     * @param id             идентификатор категории
     * @param topicsCategory категория с внесенными изменениями
     * @return ResponseEntity<TopicsCategory> возвращает измененную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/{id}")
    public ResponseEntity<TopicsCategory> updateTopicsCategory(@PathVariable(name = "id") long id, @RequestBody TopicsCategory topicsCategory) {
        if (!topicsCategoryService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topicsCategoryService.update(topicsCategory));
    }

    /**
     * Метод для пометки категории тем, как архивной
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает заархивированную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/archive/{id}")
    public ResponseEntity<TopicsCategory> archiveTopicsCategory(@PathVariable(name = "id") long id) {
        if (!topicsCategoryService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(topicsCategoryService.archive(topicsCategoryService.findById(id)));
    }

    /**
     * Метод для пометки категории тем, как актуальной
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает акутальную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/unarchive/{id}")
    public ResponseEntity<TopicsCategory> unarchiveTopicsCategory(@PathVariable(name = "id") long id) {
        if (!topicsCategoryService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(topicsCategoryService.unarchive(topicsCategoryService.findById(id)));
    }
}
