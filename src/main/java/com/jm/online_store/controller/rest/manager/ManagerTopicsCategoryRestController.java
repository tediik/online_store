package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.exception.topicsCategoryService.TopicsCategoryExceptionConstants;
import com.jm.online_store.exception.topicsCategoryService.TopicsCategoryServiceException;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.model.dto.TopicsCategoryDto;
import com.jm.online_store.service.interf.TopicsCategoryService;
import io.swagger.annotations.*;
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
     * @return ResponseEntity<List <TopicsCategory>> возвращает все
     * категории тем со статусом ответа, если категорий тем нет - только статус
     */
    @GetMapping
    @ApiOperation(value = "Get list of all categories",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 204, message = "Category with no content")
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
     * если категория тем с таким id не существует - только статус
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get category by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Category was not found")
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> readTopicsCategory(@PathVariable(name = "id") long id) {
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.findById(id), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для добавления новой категории тем
     *
     * @param topicsCategory категория тем, которая будет создана
     * @return ResponseEntity<TopicsCategory> возвращает созданную категорию тем со статусом ответа,
     * если категория тем с таким именем уже существует - только статус
     */
    @PostMapping
    @ApiOperation(value = "Add a new category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 304, message = "Category with this name is already exists")
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> createTopicsCategory(@RequestBody TopicsCategory topicsCategory) {
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.create(topicsCategory), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для изменения категории тем
     *
     * @param id             идентификатор категории
     * @param topicsCategory категория с внесенными изменениями
     * @return ResponseEntity<TopicsCategory> возвращает измененную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "Update  category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Category  name is not found"),
            @ApiResponse(code = 404, message = "Category  ID  is not found")
    })
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> updateTopicsCategory(@PathVariable(name = "id") long id,
                                                               @RequestBody TopicsCategory topicsCategory) {
        if (!topicsCategoryService.existsByCategoryName(topicsCategory.getCategoryName()))
            throw new TopicsCategoryServiceException(TopicsCategoryExceptionConstants.TOPIC_CATEGORY_NOT_FOUND);

        if (!topicsCategoryService.existsById(id)) {
            throw new TopicsCategoryServiceException(TopicsCategoryExceptionConstants.TOPIC_CATEGORY_NOT_FOUND);
        }
        TopicsCategoryDto returnValue = modelMapper.map(topicsCategoryService.update(topicsCategory), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    /**
     * Метод для пометки категории тем, как архивной
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает заархивированную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/archive/{id}")
    @ApiOperation(value = "Mark category as archived by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Category  ID  is not found")
    public ResponseEntity<ResponseDto<TopicsCategoryDto>> archiveTopicsCategory(@PathVariable(name = "id") long id) {
        if (!topicsCategoryService.existsById(id)) {
            throw new TopicsCategoryServiceException(TopicsCategoryExceptionConstants.TOPIC_CATEGORY_NOT_FOUND);
        }
        TopicsCategoryDto returnValue = modelMapper
                .map(topicsCategoryService.archive(topicsCategoryService.findById(id)), TopicsCategoryDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для пометки категории тем, как актуальной
     *
     * @param id идентификатор категории
     * @return ResponseEntity<TopicsCategory> возвращает акутальную категорию тем со статусом ответа,
     * если категория тем с таким id не существует - только статус
     */
    @PutMapping("/unarchive/{id}")
    @ApiOperation(value = "Mark category as unarchived by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Category  ID  is not found")
    public ResponseEntity<ResponseDto<TopicsCategory>> unarchiveTopicsCategory(@PathVariable(name = "id") long id) {
        if (!topicsCategoryService.existsById(id)) {
            throw new TopicsCategoryServiceException(TopicsCategoryExceptionConstants.TOPIC_CATEGORY_NOT_FOUND);
        }
        return ResponseEntity.ok(new ResponseDto<>(true , topicsCategoryService.unarchive(topicsCategoryService.findById(id))));
    }
}
