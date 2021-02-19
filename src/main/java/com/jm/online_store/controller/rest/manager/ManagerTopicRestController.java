package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.Topic;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.TopicDto;
import com.jm.online_store.service.interf.TopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController для чтения/добавления/изменения тем для обратной связи
 */
@RestController
@RequestMapping("/api/manager/topic")
@RequiredArgsConstructor
@Api(description = "Rest controller for read/add/update feedback topics")
public class ManagerTopicRestController {
    private final TopicService topicService;
    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Метод для получения единственной темы
     * @param id идентификатор темы
     * @return ResponseEntity<Topic> возвращает единственную тему со статусом ответа,
     * если темы с таким id не существует - выбросит исключенине TopicNotFoundException
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get topic by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Topic has been found"),
            @ApiResponse(code = 404, message = "Topic has not been found")
    })
    public ResponseEntity<ResponseDto<TopicDto>> readTopicById(@PathVariable(name = "id") long id) {
        TopicDto returnValue = modelMapper.map(topicService.findById(id), TopicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для добавления новой темы
     * @param topic тема, которая будет создана
     * @return ResponseEntity<TopicDto> возвращает созданную тему со статусом ответа,
     * если тема с таким именем уже существует - выбросит исключение TopicAlreadyExists
     */
    @PostMapping
    @ApiOperation(value = "Create new topic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Topic has been created"),
            @ApiResponse(code = 400, message = "Topic hasn't been created")
    })
    public ResponseEntity<ResponseDto<TopicDto>> createTopic(@RequestBody TopicDto topicReq){
        Topic topic = modelMapper.map(topicReq, Topic.class);
        TopicDto returnValue = modelMapper.map(topicService.create(topic), TopicDto.class);
        return new ResponseEntity<>(new ResponseDto<>(true ,returnValue ), HttpStatus.CREATED);
    }

    /**
     * Метод для изменения темы
     * @param topic тема с внесенными изменениями
     * @return ResponseEntity<Topic> возвращает измененную тему со статусом ответа,
     * если тема с таким id не существует - только статус
     */
    @PutMapping
    @ApiOperation(value = "Update topic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Topic has been modified"),
            @ApiResponse(code = 404, message = "Topic hasn't been found")
    })
    public ResponseEntity<ResponseDto<TopicDto>> editTopic( @RequestBody Topic topic) {
        TopicDto returnValue = modelMapper.map(topicService.update(topic), TopicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }
}
