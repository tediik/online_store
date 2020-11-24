package com.jm.online_store.service.interf;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;

import java.util.List;
import java.util.Optional;

public interface CategoriesService {

    ArrayNode getAllCategories();

    List<Categories> findAll();

    String getCategoryNameByProductId(Long productId);

    List<Categories> getCategoriesByParentCategoryId (Long parentCategoryId);

    Optional<Categories> getCategoryById(Long categoryId);

    Optional<Categories> getCategoryByCategoryName(String category);

    void saveCategory (Categories categories);

    void deleteCategory(Long idCategory);

    void saveAll(List<Categories> catList);

    void addToProduct(Product product, Long id);

    void removeFromProduct(Product product, Long id);
}
