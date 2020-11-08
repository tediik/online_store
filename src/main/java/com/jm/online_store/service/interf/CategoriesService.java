package com.jm.online_store.service.interf;

import com.jm.online_store.model.Categories;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.List;
import java.util.Optional;

public interface CategoriesService {

    JSONArray getAllCategories();//List<Categories>

    String getCategoryNameByProductId(Long productId);

    List<Categories> getCategoriesByParentCategoryId (Long parentCategoryId);

    Optional<Categories> getCategoryById(Long categoryId);

    Optional<Categories> getCategoryByCategoryName(String category);

    void saveCategory (Categories categories);

    void deleteCategory(Long idCategory);

    void saveAll(List<Categories> catList);
}
