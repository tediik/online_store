package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.util.Transliteration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер страницы подкатегории.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class SubcategoryPageRestController {

    private final CategoriesService categoriesService;

    private String categoryName;

    /**
     * Получает имя подкатегории по адресу, на который его запостил метод js, получив последнюю чатсть URL
     *
     * @param name имя категории из тела запроса
     */
    @PostMapping("/categoryName")
    public void getCategoryName(@RequestBody String name) {
        categoryName = name.replaceAll("\"", "");
    }

    /**
     * Ищет категорию по имени из пути, переводя транслит латиницей на слово кириллицей с помощью утильного класса {@link Transliteration}
     *
     * @return сущность Categories
     */
    @GetMapping("/category")
    public ResponseEntity<Categories> getCategory() {
        if (!categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(categoryName)).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Categories category = categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(categoryName)).get();
        return ResponseEntity.ok(category);
    }

    /**
     * Метод поиска всех категорий в БД
     *
     * @return список Categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = categoriesService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
