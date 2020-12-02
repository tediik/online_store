package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.util.Transliteration;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер страницы подкатегории
 */
@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class SubcategoryPageRestController {

    private CategoriesService categoriesService;

    /**
     * Ищет категорию в БД по имени из пути, переводя транслит латиницей на слово кириллицей
     * с помощью метода утильного класса {@link Transliteration}
     *
     * @param name имя подкатегории
     * @return сущность Categories, если категория найдена
     * @author Dmitriy (dshishkaryan)
     */
    @GetMapping("/{name}")
    public ResponseEntity<Categories> getCategory(@PathVariable String name) {
        String categoryName = name.replaceAll("\"", "");
        ResponseEntity<Categories>[] answer = new ResponseEntity[1];
        categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(categoryName)).ifPresentOrElse(
                value -> answer[0] = ResponseEntity.ok(value), () -> answer[0] = ResponseEntity.notFound().build());
        return answer[0];
    }

    /**
     * Метод поиска всех категорий в БД
     *
     * @return список Categories
     */

    @GetMapping("/categories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        return ResponseEntity.ok(categoriesService.findAll());
    }
}
