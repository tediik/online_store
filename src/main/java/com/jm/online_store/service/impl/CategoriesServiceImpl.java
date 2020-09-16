package com.jm.online_store.service.impl;

import com.jm.online_store.model.Categories;
import com.jm.online_store.repository.CategoriesRepository;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    public Optional<Categories> getCategoryById(Long categoryId) {
        return categoriesRepository.findById(categoryId);
    }

    public Optional<Categories> getCategoryByCategoryName(String category) {
        return categoriesRepository.findByCategory(category);
    }

    public void saveCategory(Categories categories) {
        categoriesRepository.save(categories);
    }

    public void deleteCategory(Long idCategory) {
        categoriesRepository.deleteById(idCategory);
    }

    public void saveAll(List<Categories> catList) {
        categoriesRepository.saveAll(catList);
    }
}
