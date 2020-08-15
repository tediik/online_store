package com.jm.online_store.service;

import com.jm.online_store.model.Categories;
import com.jm.online_store.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriesService {
    @Autowired
    CategoriesRepository categoriesRepository;

    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public Optional<Categories> getProductByCategoryId(Long categoryId) {
        return categoriesRepository.findById(categoryId);
    }

    public void saveCategory (Categories categories){
        categoriesRepository.save(categories);
    }

    public void deleteCategory(Long idCategory){
        categoriesRepository.deleteById(idCategory);
    }
}
