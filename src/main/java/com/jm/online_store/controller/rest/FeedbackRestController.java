package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.Response;
import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.dto.FeedbackDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.TopicDto;
import com.jm.online_store.model.dto.TopicsCategoryDto;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicsCategoryService;
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
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
@Api(description = "Rest controller for customers Feedbacks")
public class FeedbackRestController {
    private final FeedbackService feedbackService;
    private final TopicsCategoryService topicsCategoryService;
    private final ModelMapper modelMapper;
    private final Type TopicCategoryListType = new TypeToken<List<TopicsCategoryDto>>() {}.getType();
    private final Type TopicListType = new TypeToken<List<TopicDto>>() {}.getType();
    private final Type FeedbackListType = new TypeToken<List<FeedbackDto>>() {}.getType();

    /**
     * Mapping to get categories from {@link TopicsCategory}
     *
     * @return - ResponseEntity with list of actual categories for feedback
     */
    @GetMapping("/categories")
    @ApiOperation(value = "gets categories from TopicsCategory",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<TopicsCategoryDto>>> getFeedbackTopicsCategories() {
        List<TopicsCategoryDto> returnValue = modelMapper.map(topicsCategoryService.findAllByActualIsTrue(), TopicCategoryListType);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.OK);
    }

    /**
     * Mapping to get topics list for special category
     *
     * @param category - {@link String} String with name of category
     * @return - ResponseEntity with list of topics for feedback
     */
    @GetMapping("/{category}")
    @ApiOperation(value = "gets list of topics for feedback of the current category",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<TopicDto>>> getTopicsByCategory(@PathVariable String category) {
        List<TopicDto> returnValue = modelMapper.map(topicsCategoryService.findByCategoryName(category).getTopics(), TopicListType);
        return new ResponseEntity<>(new ResponseDto<>(
                true, returnValue), HttpStatus.OK);
    }

    /**
     * Mapping to post new feedback
     *
     * @param newFeedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PostMapping
    @ApiOperation(value = "post new feedback",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<FeedbackDto>> addNewFeedback(@RequestBody Feedback newFeedback) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(feedbackService.addFeedbackFromDto(newFeedback), FeedbackDto.class)), HttpStatus.OK);
    }

    /**
     * Метод добавления ответа на обращения
     * @param feedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PutMapping("/addAnswer")
    @ApiOperation(value = "Adds answer for the feedback",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<FeedbackDto>> addAnswerFeedback(@RequestBody Feedback feedback) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(feedbackService.addAnswerFeedback(feedback), FeedbackDto.class)), HttpStatus.OK);
    }

    /**
     * Метод устанавливает дату и время обращения, для поля responseExpected
     * @param feedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PutMapping("/laterAnswer")
    @ApiOperation(value = "Sets the date and the time of the feedback for the \"responseExpected\" field",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<FeedbackDto>> updateTimeAnswerFeedback(@RequestBody Feedback feedback) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(feedbackService.updateTimeAnswerFeedback(feedback), FeedbackDto.class)), HttpStatus.OK);
    }

    /**
     * Метод возвращает обращение в работу
     * @param id - идентификатор обращения
     * @return ResponseEntity
     */
    @PutMapping("/inWork")
    @ApiOperation(value = "Returns the feedback back at work",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<FeedbackDto>> returnInWork(@RequestBody Long id) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(feedbackService.returnInWork(id), FeedbackDto.class)), HttpStatus.OK);

    }

    /**
     * Метод возвращает обращения текущего пользователя
     * @return ResponseEntity<ResponseDto<List<FeedbackDto>>>
     */
    @GetMapping("/messages")
    @ApiOperation(value = "gets all feedbacks from the current customer",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FeedbackDto>>> getAllFeedbackCurrentCustomer() {
        List<FeedbackDto> returnValue = modelMapper.map(feedbackService.getAllFeedbackCurrentCustomer(), FeedbackListType);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.OK);
    }

    /**
     * Метод возвращает все обращения
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/allMessages")
    @ApiOperation(value = "gets all feedbacks",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FeedbackDto>>> getAllFeedback() {
        List<FeedbackDto> feedbackList = modelMapper.map(feedbackService.getAllFeedback(), FeedbackListType);
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
    }

    /**
     * Метод возвращает все обращения в статуте IN_PROGRESS
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/inProgress")
    @ApiOperation(value = "gets all feedbacks that are in \"IN_PROGRESS\" status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FeedbackDto>>> getInProgressFeedback() {
        List<FeedbackDto> feedbackList = modelMapper.map(feedbackService.getInProgressFeedback(), FeedbackListType);
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
    }

    /**
     * Метод возвращает все обращения в статуте LATER
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/later")
    @ApiOperation(value = "gets all feedbacks that are in \"LATER\" status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FeedbackDto>>> getLaterFeedback() {
        List<FeedbackDto> feedbackList = modelMapper.map(feedbackService.getLaterFeedback(), FeedbackListType);
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
    }

    /**
     * Метод возвращает все обращения в статуте RESOLVED
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/resolved")
    @ApiOperation(value = "gets all feedbacks that are in \"RESOLVED\" status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FeedbackDto>>> getResolvedFeedback() {
        List<FeedbackDto> feedbackList = modelMapper.map(feedbackService.getResolvedFeedback(), FeedbackListType);
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
    }

    /**
     * Метод по идентификатору находит обращение, смотрит значение поля responseExpected,
     * если оно null, возвращаем текущее время, обрезанное до минут, иначе возвращаем
     * значение поля.
     * @param id идентификатор обращения
     * @return ResponseEntity<LocalDateTime>
     */
    @GetMapping("/timeAnswer/{id}")
    @ApiOperation(value = "gets by feedbacks id field \"responseExpected\". if it's null, returns current time",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<LocalDateTime>> getResponseExpectedFeedback(@PathVariable Long id) {
        LocalDateTime localDateTime = feedbackService.getDateTimeFeedback(id);
        return new ResponseEntity<>(new ResponseDto<>(true, localDateTime), HttpStatus.OK);
    }

    /**
     * Метод удаляет обращение
     * @param id идентификатор обращения
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "deletes feedback by id",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedbackById(id);
        return new ResponseEntity<>(new ResponseDto<>(true, Response.NO_ERROR.getText()), HttpStatus.OK);
    }
}
