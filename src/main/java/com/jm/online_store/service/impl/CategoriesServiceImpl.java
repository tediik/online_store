package com.jm.online_store.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.exception.CategoriesNotFoundException;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.CategoriesRepository;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private static final String loadPictureFrom = ".." + File.separator + "uploads" +
            File.separator + "images" + File.separator + "products" + File.separator;

    private final CategoriesRepository categoriesRepository;

    /**
     * Метод возвращает все категории без излишней информации
     */
    @Override
    public ArrayNode getAllCategories() {
        List<Categories> categoriesList = categoriesRepository.findAll();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Categories categories : categoriesList) {
            ObjectNode root = mapper.createObjectNode();
            root.put("id", categories.getId());
            root.put("parentId", categories.getParentCategoryId());
            root.put("text", categories.getCategory());
            root.put("depth", categories.getDepth());
            root.put("hasProduct", !categories.getProducts().isEmpty());
            arrayNode.add(root);
        }
        return arrayNode;
    }

    /**
     * Метод возвращает все категории
     */
    @Override
    public List<Categories> findAll() {
        return categoriesRepository.findAll();
    }

    /**
     * Метод обновляет категорию
     */
    @Override
    @Transactional
    public void updateCategory(Categories category) {
        categoriesRepository.save(category);
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
     * Метод получения списка категорий без родительских
     * @return List<Categories>
     */
    @Override
    public List<Categories> getCategoriesWithoutParentCategory() {
        return categoriesRepository.getCategoriesWithoutParentCategory();
    }

    /**
     * Метод сохраняет категорию
     * @param categories категория товара
     */
    @Override
    public Categories saveCategory(Categories categories) {
        return categoriesRepository.save(categories);
    }

    /**
     * Метод удаляет категорию по её id
     * @param idCategory идентификатор категории
     */
    @Override
    public boolean deleteCategory(Long idCategory) {
        Optional<Categories> optCategories = getCategoryById(idCategory);
        if (optCategories.isEmpty()) {
            return false;
        }
        categoriesRepository.deleteById(idCategory);
        return true;
    }

    /**
     * Метод сохраняет список категорий
     * @param catList список категорий товара
     */
    @Override
    public void saveAll(List<Categories> catList) {
        catList.stream().filter(categories -> categories.getProducts() != null)
                .forEach(categories -> categories.getProducts().
                        forEach(product -> product.setProductPictureName(loadPictureFrom + "00.jpg")));
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
     * Метод добавляет категорию продукту
     * @param product продукт
     * @param category наименование категории
     */
    @Override
    public void addToProduct(Product product, String category) {
        Categories thisCat = getCategoryByCategoryName(category).orElseThrow(CategoriesNotFoundException::new);

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
