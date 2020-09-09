package com.jm.online_store.controller.simple;

import com.jm.online_store.controller.rest.SubcategoryPageRestController;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.util.Transliteration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер подкатегорий продуктов
 */
@Controller
@AllArgsConstructor
@RequestMapping("/categories")
public class SubcategoryPageController {

    private final CategoriesService categoriesService;

    /**
     * Возвращает страницу подкатегории или страницу 404, если категории с таким именем нет в БД.
     * Перед использованием для поиска наименование категории переводится в кириллицу с помощью метода класса {@link Transliteration}
     *
     * @param categoryName имя категории латиницей из адреса запроса
     * @return название страницы
     */
    @GetMapping("/{categoryName}")
    public String getSubcategoryPage(@PathVariable(value = "categoryName") String categoryName) {
        if (!categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(categoryName).replaceAll("_", " ")).isPresent()) {
            return "/error/404";
        }
        SubcategoryPageRestController.setCategoryName(categoryName);
        return "subcategoryPage";
    }
}
