package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.impl.CategoriesServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RestController для добавления/изменения/удаления категорий товаров
 */
@RestController
@RequestMapping("/api/manager/categories")
@RequiredArgsConstructor
public class ManagerCategoriesRestController {
    private final CategoriesServiceImpl categoriesService;

    /**
     * Метод для получения всех категорий товаров
     * @return ResponseEntity<List<Categories>> возвращает все категории со статусом ответа,
     * если категорий нет - только статус
     */
    @GetMapping
    public ResponseEntity<List<Categories>> readAll(){
        List<Categories> categories = categoriesService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * Метод для получения конкретной категории
     * @param id идентификатор категории
     * @return ResponseEntity<Categories> возвращает конкретную категорию со статусом ответа,
     * если категории нет - только статус
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categories> readUser(@PathVariable(name = "id") long id) {
        if (!categoriesService.categoryExistById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoriesService.getCategoryById(id).get());
    }

    /**
     * Метод для добавления новой категории
     * @param category категория для добавления
     * @return ResponseEntity<Categories> возвращает созданную категорию со статусом,
     * если такая категория уже существует - только статус
     */
    @PostMapping
    public ResponseEntity<Categories> createCategory(@RequestBody Categories category) {
        if (categoriesService.categoryExistByCategory(category.getCategory())){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        categoriesService.saveCategory(category);
        return ResponseEntity.ok(categoriesService.getCategoryByCategoryName(category.getCategory()).get());
    }

    /**
     * Метод для изменения существующей категории
     * @param id идентификатор категории
     * @param category измененная категория
     * @return ResponseEntity<Categories> возвращает измененную категорию со статусом,
     * если категории нет - только статус
     */
    @PutMapping("/{id}")
    public ResponseEntity<Categories> updateCategory(@PathVariable(name = "id") long id, @RequestBody Categories category) {
        if (!categoriesService.categoryExistById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoriesService.saveCategory(category);
        return ResponseEntity.ok(category);
    }

    /**
     * Метод для удаления конкретной категории
     * @param id идентификатор категории
     * @return ResponseEntity<Categories> возвращает статус ответа
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Categories> deleteUser(@PathVariable(name = "id") long id) {
        if (!categoriesService.categoryExistById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoriesService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
