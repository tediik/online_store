package com.jm.online_store.service.impl;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.CategoriesRepository;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    /**
     * Метод возвращает все категории без излишней информации
     */
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

    /**
     * Метод возвращает имя категории по id продукта
     * @param productId идентификатор продукта
     */
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

    /**
     * Метод возвращает список подкатегорий для родительской категории
     * @param parentCategoryId идентификатор категории
     */
    @Override
    public List<Categories> getCategoriesByParentCategoryId(Long parentCategoryId) {
        return categoriesRepository.getCategoriesByParentCategoryId(parentCategoryId);
    }

    /**
     * Метод возвращает категорию по её id
     * @param categoryId идентификатор категории
     */
    @Override
    public Optional<Categories> getCategoryById(Long categoryId) {
        return categoriesRepository.findById(categoryId);
    }

    /**
     * Метод возвращает категорию в Optional
     * @param category имя категории товара
     */
    @Override
    public Optional<Categories> getCategoryByCategoryName(String category) {
        return categoriesRepository.findByCategory(category);
    }

    /**
     * Метод сохраняет категорию
     * @param categories категория товара
     */
    @Override
    public void saveCategory(Categories categories) {
        categoriesRepository.save(categories);
    }

    /**
     * Метод удаляет категорию по её id
     * @param idCategory идентификатор категории
     */
    @Override
    public void deleteCategory(Long idCategory) {
        categoriesRepository.deleteById(idCategory);
    }

    /**
     * Метод сохраняет список категорий
     * @param catList список категорий товара
     */
    @Override
    public void saveAll(List<Categories> catList) {
        categoriesRepository.saveAll(catList);
    }

    /**
     * Метод добавляет категорию продукту
     * @param product продукт
     * @param id идентификатор категории
     */
    @Override
    public void addToProduct(Product product, Long id) {
        Categories thisCat = getCategoryById(id).get();

        List<Product> thisCatProducts = thisCat.getProducts();
        thisCatProducts.add(product);
        thisCat.setProducts(thisCatProducts);
        saveCategory(thisCat);
    }

    /**
     * Метод удаляет категорию продукта
     * @param product продукт
     * @param id идентификатор категории
     */
    @Override
    public void removeFromProduct(Product product, Long id) {
        Categories thisCat = getCategoryById(id).get();
        List<Product> productList = thisCat.getProducts();
        productList.remove(product);
        thisCat.setProducts(productList);
        saveCategory(thisCat);
    }
}
