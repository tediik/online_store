package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.TopicsCategoryDto;
import com.jm.online_store.service.interf.TopicsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * RestController для чтения/добавления/изменения/архивирования категорий тем для обратной связи
 */
@RestController
@RequestMapping("/api/manager/topicsCategory")
@RequiredArgsConstructor
@Api(description = "Rest controller for read/add/update categories for feedback topics")
public class ManagerTopicsCategoryRestController {
    private final TopicsCategoryService topicsCategoryService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Type listType = new TypeToken<List<TopicsCategoryDto>>() {}.getType();


    /**
     * Метод для получения всех категорий тем
     *
     * @return ResponseEntity<List <TopicsCategoryDto>> возвращает все
     * категории тем со статусом ответа, если категорий тем нет - пустой список
     */
    @GetMapping
    @ApiOperation(value = "Get list of all categories",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TopicCategories were found"),
            @ApiResponse(code = 200, message = "TopicCategories were not found")
    })
    public ResponseEntity<ResponseDto<List<TopicsCategoryDto>>> readAllTopicsCategories() {
        List<TopicsCategory> topicsCategories = topicsCategoryService.findAll();
        List<TopicsCategoryDto> returnValue = modelMapper.map(topicsCategories, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для получения единственной категории тем
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает единственную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - выбросит исключение TopicNotFoundException
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get category by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TopicCategory was found"),
            @ApiResponse(code = 404, message = "TopicCategory was not not found")
    })
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> readTopicsCategory(@PathVariable(name = "id") long id) {
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.findById(id), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для добавления новой категории тем
     *
     * @param topicsCategoryReq категория тем, которая будет создана
     * @return ResponseEntity<TopicsCategory> возвращает созданную категорию тем со статусом ответа,
     * если категория тем с таким именем уже существует - выбросит исключение TopicCategoryAlreadyExists
     */
    @PostMapping
    @ApiOperation(value = "Add a new category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "TopicCategory was found"),
            @ApiResponse(code = 400, message = "TopicCategory with this name is already exists")
    })
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> createTopicsCategory(@RequestBody TopicsCategoryDto topicsCategoryReq) {
        TopicsCategory topicsCategory = modelMapper.map(topicsCategoryReq, TopicsCategory.class);
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.create(topicsCategory), TopicsCategoryDto.class);
        return new ResponseEntity<>(new ResponseDto<>(true , returnValue), HttpStatus.CREATED);
    }

    /**
     * Метод для изменения категории тем
     *
     * @param id             идентификатор категории
     * @param topicsCategoryReq категория с внесенными изменениями
     * @return ResponseEntity<TopicsCategory> возвращает измененную категорию тем со статусом ответа,
     * если категория тем с таким id или именем не существует - бросает исключение TopicCategoryNotFoundException
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "Update  category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TopicCategory was modified"),
            @ApiResponse(code = 400, message = "TopicCategory was not modified"),
            @ApiResponse(code = 404, message = "TopicCategory id is not found")
    })
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> updateTopicsCategory(@PathVariable(name = "id") long id,
                                                                               @RequestBody TopicsCategoryDto topicsCategoryReq) {
        TopicsCategory gotBack = topicsCategoryService.updateById(id, modelMapper.map(topicsCategoryReq, TopicsCategory.class));
        return ResponseEntity.ok(new ResponseDto<>(true , modelMapper.map(gotBack, TopicsCategoryDto.class)));
    }


    /**
     * Метод для пометки категории тем, как архивной
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает заархивированную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - бросает исключение TopicCategoryNotFoundException или
     * TopicCategoryAlreadyExists если пытаются архивировать то что уже в архиве
     */
    @PutMapping("/archive/{id}")
    @ApiOperation(value = "Mark category as archived by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TopicCategory was archived"),
            @ApiResponse(code = 400, message = "TopicCategory was not archived"),
            @ApiResponse(code = 404, message = "TopicCategory id is not found")
    })
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> archiveTopicsCategory(@PathVariable(name = "id") Long id) {
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.archive(id), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для пометки категории тем, как актуальной
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает акутальную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - бросает исключение TopicCategoryNotFoundException или
     * TopicCategoryAlreadyExists если пытаются деархивировать то что уже не в архиве
     */
    @PutMapping("/unarchive/{id}")
    @ApiOperation(value = "Mark category as unarchived by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "TopicCategory was unarchived"),
            @ApiResponse(code = 400, message = "TopicCategory was not unarchived"),
            @ApiResponse(code = 404, message = "TopicCategory id is not found")
    })
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> unarchiveTopicsCategory(@PathVariable(name = "id") Long id) {
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.unarchive(id), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }
}
