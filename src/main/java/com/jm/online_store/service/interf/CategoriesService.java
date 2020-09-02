package com.jm.online_store.service.interf;

import com.jm.online_store.model.Categories;

import java.util.List;
import java.util.Optional;

public interface CategoriesService {

    List<Categories> getAllCategories();

    Optional<Categories> getCategoryById(Long categoryId);

    Long getIdBySuperCategory(String superCategoryName);

    void saveCategory (Categories categories);

    void deleteCategory(Long idCategory);

    void saveAll(List<Categories> catList);
}
