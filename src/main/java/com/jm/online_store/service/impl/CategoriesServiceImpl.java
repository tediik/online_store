package com.jm.online_store.service.impl;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.CategoriesRepository;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Override
    public JSONArray getAllCategories() {
        JSONArray resultArray = new JSONArray();
        List<Categories> categoriesList = categoriesRepository.findAll();
        for (Categories categories : categoriesList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", categories.getId());
            jsonObject.put("parentId", categories.getParentCategoryId());
            jsonObject.put("text", categories.getCategory());
            jsonObject.put("depth", categories.getDepth());
            jsonObject.put("hasProduct", !categories.getProducts().isEmpty());
            resultArray.add(jsonObject);
        }
        return resultArray;
    }

    @Override
    public String getCategoryNameByProductId(Long productId) {
        List<Categories> categoriesList = categoriesRepository.findAll();
        for (Categories categories : categoriesList) {
            List<Product> listOfProducts = categories.getProducts();
            if (!listOfProducts.isEmpty()) {
                for (Product product : listOfProducts) {
                    if (product.getId() == productId) {
                        return categories.getCategory();
                    }
                }
            }
        }
        return "";
    }

    @Override
    public List<Categories> getCategoriesByParentCategoryId(Long parentCategoryId) {
        return categoriesRepository.getCategoriesByParentCategoryId(parentCategoryId);
    }

    @Override
    public Optional<Categories> getCategoryById(Long categoryId) {
        return categoriesRepository.findById(categoryId);
    }

    @Override
    public Optional<Categories> getCategoryByCategoryName(String category) {
        return categoriesRepository.findByCategory(category);
    }

    @Override
    public void saveCategory(Categories categories) {
        categoriesRepository.save(categories);
    }

    @Override
    public void deleteCategory(Long idCategory) {
        categoriesRepository.deleteById(idCategory);
    }

    @Override
    public void saveAll(List<Categories> catList) {
        categoriesRepository.saveAll(catList);
    }


}
