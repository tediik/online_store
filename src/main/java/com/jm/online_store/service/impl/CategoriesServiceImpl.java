package com.jm.online_store.service.impl;

import com.jm.online_store.model.Categories;
import com.jm.online_store.repository.CategoriesRepository;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    private JSONObject jsonObject = new JSONObject();
    private JSONArray resultArray = new JSONArray();

    @Override
    public JSONArray getAllCategories() {

        List<Categories> categoriesList = categoriesRepository.findAll();

        JSONArray childArray = new JSONArray();
        int count = 0;

        StringBuilder sb = new StringBuilder();

        for (Categories categories : categoriesList) {
            List<Categories> childrenCat = categoriesList.stream().filter(p -> p.getParentCategoryId() == categories.getId())
                    .collect(Collectors.toList());

            if (!childrenCat.isEmpty()) {
                for (Categories child : childrenCat) {
                    JSONObject childObject = new JSONObject();
                    childObject.put("id", child.getId());
                    childObject.put("text", child.getCategory());
                    childArray.add(childObject);
                }
            }
            jsonObject.put("children", childArray);
            jsonObject.put("text", categories.getCategory());
            jsonObject.put("id", categories.getId());

            //System.out.println(" " + jsonObject);

            resultArray.add(count++, jsonObject);

//            resultArray.stream().forEach(System.out::println);
            //System.out.println("after child add RESULTARRAY - " + resultArray.toString());
            sb.append(resultArray.toString());
            childArray.clear();
            jsonObject.clear();


            //System.out.println("sb - " + sb);
        }

        System.out.println("sb - " + sb);
        System.out.println();
        //System.out.println("resultArray after cycle- " + resultArray);
        //resultArray.stream().forEach(System.out::println);
        return resultArray;
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
