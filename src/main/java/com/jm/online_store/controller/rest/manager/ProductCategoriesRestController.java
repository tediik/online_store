package com.jm.online_store.controller.rest.manager;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.dto.CategoriesDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.TopicsCategoryDto;
import com.jm.online_store.service.interf.CategoriesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

/**
 * REST-контроллер для управления категориями товаров со страницы менеджера
 */
@RestController
@RequestMapping("api/categories")
@AllArgsConstructor
@Slf4j
@Api(value = "Rest controller for actions from prod page")
public class ProductCategoriesRestController {
    private final CategoriesService categoriesService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Type listType = new TypeToken<List<TopicsCategoryDto>>() {}.getType();
    /**
     * Возвращает список всех категорий
     */
    @GetMapping("/all")
    @ApiOperation(value = "return all categories",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<ArrayNode>> getAllCategories() {
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesService.getAllCategories()));
    }

    /**
     * Возвращает категорию по id
     * @param id
     */
    @GetMapping("/getOne/{id}")
    @ApiOperation(value = "return name of category by product's id",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> getCategoryNameByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesService.getCategoryNameByProductId(id)));
    }

    /**
     * Возвращает список подкатегорий для корневой категории
     */
    @GetMapping("/sub/{id}")
    @ApiOperation(value = "return list of sub categories",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<Categories>>> getSubCategoriesById(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesService.getCategoriesByParentCategoryId(id)));
    }

    /**
     * Сохраняет категорию
     */
    @PostMapping
    @ApiOperation(value = "save category",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CategoriesDto>> newCategory(@RequestBody Categories categories) {
        CategoriesDto returnValue = modelMapper.map(categoriesService.saveCategory(categories), CategoriesDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Обновление категории
     */
    @PutMapping
    @ApiOperation(value = "update category",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CategoriesDto>> updateCategory(@RequestBody Categories categories) {
        CategoriesDto returnValue = modelMapper.map(categoriesService.saveCategory(categories), CategoriesDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Удаление категории
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "delete category",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> deleteCategory(@PathVariable Long id) {
        return categoriesService.deleteCategory(id) ?
                ResponseEntity.ok(new ResponseDto<>(true ,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id))) :
                ResponseEntity.ok(new ResponseDto<>(false,
                String.format(ResponseOperation.HAS_NOT_BEEN_DELETED.getMessage(), id)));
    }
}
