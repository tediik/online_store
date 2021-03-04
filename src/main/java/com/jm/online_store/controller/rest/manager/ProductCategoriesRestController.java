package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.dto.CategoriesDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CategoriesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import java.util.List;
import java.util.stream.Collectors;

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
    private final ModelMapper modelMapper;

    /**
     * Метод для получения списка всех Categories
     * @return ResponseEntity<ResponseDto<ArrayNode>> возвращает все
     * категории со статусом ответа
     */
    @GetMapping("/all")
    @ApiOperation(value = "returns all categories",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Categories have been found"),
            @ApiResponse(code = 200, message = "Categories haven't been found")
    })
    public ResponseEntity<ResponseDto<ArrayNode>> getAllCategories() {
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesService.getAllCategories()));
    }

    /**
     * Метод по id продукта находит название категории
     * @param id продукта
     * @return String название категории по id продукта
     */
    @GetMapping("/getOne/{id}")
    @ApiOperation(value = "returns name of category by product's id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Name has been found"),
            @ApiResponse(code = 200, message = "Name hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> getCategoryNameByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesService.getCategoryNameByProductId(id)));
    }

    /**
     * Метод находит список подкатегорий по id
     * или пустой список
     * @param id саб категории
     * @return Возвращает список подкатегорий для корневой категории
     */
    @GetMapping("/sub/{id}")
    @ApiOperation(value = "return list of sub categories",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns list of sub categories"),
            @ApiResponse(code = 200, message = "Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<Categories>>> getSubCategoriesById(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesService.getCategoriesByParentCategoryId(id)));
    }

    /**
     * Метод сохраняет категорию
     * @param categoriesReq категория для сохранения в БД
     * @return сохраненную сущность
     */
    @PostMapping
    @ApiOperation(value = "save category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 201, message = "Create new category")
    public ResponseEntity<ResponseDto<CategoriesDto>> newCategory(@RequestBody CategoriesDto categoriesReq) {
        Categories categories = modelMapper.map(categoriesReq, Categories.class);
        CategoriesDto returnValue = modelMapper.map(categoriesService.saveCategory(categories), CategoriesDto.class);
        return new ResponseEntity<>(new ResponseDto<>(true , returnValue) , HttpStatus.CREATED);
    }

    /**
     * Метод обновляет категорию
     * @param categoriesReq катогория для обновления в БД
     * @return обновленную сущность
     */
    @PutMapping
    @ApiOperation(value = "update category",
            authorizations = { @Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category has been updated"),
            @ApiResponse(code = 400, message = "Category hasn't been updated")
    })
    public ResponseEntity<ResponseDto<CategoriesDto>> updateCategory(@RequestBody CategoriesDto categoriesReq) {
        Categories categories = modelMapper.map(categoriesReq, Categories.class);
        CategoriesDto returnValue = modelMapper.map(categoriesService.updateCategory(categories), CategoriesDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод удаляет категорию по id
     * @param id идентификатор катеогории для удаления
     * @return String описание результата операции
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "delete category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category has been deleted"),
            @ApiResponse(code = 200, message = "Category hasn't been deleted"),
            @ApiResponse(code = 404, message = "Category hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> deleteCategory(@PathVariable Long id) {
        return categoriesService.deleteCategory(id) ?
                ResponseEntity.ok(new ResponseDto<>(true ,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id))) :
                ResponseEntity.ok(new ResponseDto<>(false,
                String.format(ResponseOperation.HAS_NOT_BEEN_DELETED.getMessage(), id)));
    }

    /**
     * Метод находит все категории, не имеющие родительские категии
     * или пустой список
     * @return Возвращает список категорий, не имеющих родительских категийй
     */
    @GetMapping("/withoutParentCategory")
    @ApiOperation(value = "return list of categories without parent category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Returns list of  categories without parent category"),
            @ApiResponse(code = 200, message = "Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<CategoriesDto>>> getCategoriesWithoutParentCategory() {
        List<CategoriesDto> categoriesWithoutParentCategory  = categoriesService.getCategoriesWithoutParentCategory()
                .stream()
                .map(category -> modelMapper.map(category, CategoriesDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ResponseDto<>(true, categoriesWithoutParentCategory));
    }
}