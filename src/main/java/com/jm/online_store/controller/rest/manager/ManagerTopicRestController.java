package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.Topic;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.TopicDto;
import com.jm.online_store.service.interf.TopicService;
import io.swagger.annotations.*;
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
     *
     * @param id идентификатор темы
     * @return ResponseEntity<Topic> возвращает единственную тему со статусом ответа,
     * если темы с таким id не существует - только статус
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get topic by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Topic was not found")
    public ResponseEntity<ResponseDto<TopicDto>> readTopicById(@PathVariable(name = "id") long id) {
        Topic topicFromService = topicService.findById(id);
        TopicDto returnValue = modelMapper.map(topicFromService, TopicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для добавления новой темы
     *
     * @param topic тема, которая будет создана
     * @return ResponseEntity<Topic> возвращает созданную тему со статусом ответа,
     * если тема с таким именем уже существует - только статус
     */
    @PostMapping
    @ApiOperation(value = "Create new topic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 304, message = "Topic was not modified")
    public ResponseEntity<ResponseDto<TopicDto>> createTopic(@RequestBody Topic topic){
        TopicDto returnValue = modelMapper.map(topicService.create(topic), TopicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для изменения темы
     *
     * @param id идентификатор темы
     * @param topic тема с внесенными изменениями
     * @return ResponseEntity<Topic> возвращает измененную тему со статусом ответа,
     * если тема с таким id не существует - только статус
     */
    @PutMapping
    @ApiOperation(value = "Update topic by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Topic was not modified"),
            @ApiResponse(code = 404, message = "Topic was not found")
    })
    public ResponseEntity<ResponseDto<TopicDto>> editTopic( @RequestBody Topic topic) {
        TopicDto returnValue = modelMapper.map(topicService.update(topic), TopicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }
}
