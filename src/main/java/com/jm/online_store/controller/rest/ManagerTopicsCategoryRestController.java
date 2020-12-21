package com.jm.online_store.controller.rest;

import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.service.interf.TopicsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(description = "Rest controller for read/add/update categories for feedback topics")
public class ManagerTopicsCategoryRestController {
    private final TopicsCategoryService topicsCategoryService;

    /**
     * Метод для получения всех категорий тем
     *
     * @return ResponseEntity<List <TopicsCategory>> возвращает все
     * категории тем со статусом ответа, если категорий тем нет - только статус
     */
    @GetMapping
    @ApiOperation(value = "Get list of all categories")
    @ApiResponse(code = 204, message = "Category with no content")
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
    @ApiOperation(value = "Get category by ID")
    @ApiResponse(code = 404, message = "Category was not found")
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
    @ApiOperation(value = "Add a new category")
    @ApiResponse(code = 304, message = "Category with this name is already exists")
    public ResponseEntity<TopicsCategory> createTopicsCategory(@RequestBody TopicsCategory topicsCategory) {
        if (topicsCategoryService.existsByCategoryName(topicsCategory.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        return ResponseEntity.ok(topicsCategoryService.create(topicsCategory));
    }

    /**
     * Метод для изменения категории тем
     *
     * @param id             идентификатор категории
     * @param topicsCategory категория с внесенными изменениями
     * @return ResponseEntity<TopicsCategory> возвращает измененную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "Update  category")
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Category  name is not found"),
            @ApiResponse(code = 404, message = "Category  ID  is not found")
    })
    public ResponseEntity<TopicsCategory> updateTopicsCategory(@PathVariable(name = "id") long id, @RequestBody TopicsCategory topicsCategory) {
        if (topicsCategoryService.existsByCategoryName(topicsCategory.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
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
    @ApiOperation(value = "Mark category as archived by ID")
    @ApiResponse(code = 404, message = "Category  ID  is not found")
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
    @ApiOperation(value = "Mark category as unarchived by ID")
    @ApiResponse(code = 404, message = "Category  ID  is not found")
    public ResponseEntity<TopicsCategory> unarchiveTopicsCategory(@PathVariable(name = "id") long id) {
        if (!topicsCategoryService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(topicsCategoryService.unarchive(topicsCategoryService.findById(id)));
    }
}
