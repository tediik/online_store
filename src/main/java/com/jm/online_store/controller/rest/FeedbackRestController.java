package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicsCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
@Api(description = "Rest controller for customers Feedbacks")
public class FeedbackRestController {
    private final FeedbackService feedbackService;
    private final TopicsCategoryService topicsCategoryService;

    /**
     * Mapping to get categories from {@link TopicsCategory}
     *
     * @return - ResponseEntity with list of actual categories for feedback
     */
    @GetMapping("/categories")
    @ApiOperation(value = "gets categories from TopicsCategory",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<TopicsCategory>>> getFeedbackTopicsCategories() {
        return new ResponseEntity<>(new ResponseDto<>(
                true, topicsCategoryService.findAllByActualIsTrue()), HttpStatus.OK);
        //return ResponseEntity.ok(topicsCategoryService.findAllByActualIsTrue());
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
    public ResponseEntity<ResponseDto<List<Topic>>> getTopicsByCategory(@PathVariable String category) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, topicsCategoryService.findByCategoryName(category).getTopics()), HttpStatus.OK);
        //return ResponseEntity.ok(topicsCategoryService.findByCategoryName(category).getTopics());
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
    public ResponseEntity<ResponseDto<Feedback>> addNewFeedback(@RequestBody Feedback newFeedback) {
//        feedbackService.addFeedbackFromDto(newFeedback);
        return new ResponseEntity<>(new ResponseDto<>(
                true, feedbackService.addFeedbackFromDto(newFeedback)), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    /**
     * Метод добавления ответа на обращения
     * @param feedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PutMapping("/addAnswer")
    @ApiOperation(value = "Adds answer for the feedback",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<Feedback>> addAnswerFeedback(@RequestBody Feedback feedback) {
        feedbackService.addAnswerFeedback(feedback);
        return new ResponseEntity<>(new ResponseDto<>(
                true, feedbackService.addAnswerFeedback(feedback)), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    /**
     * Метод устанавливает дату и время обращения, для поля responseExpected
     * @param feedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PutMapping("/laterAnswer")
    @ApiOperation(value = "Sets the date and the time of the feedback for the \"responseExpected\" field",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<Feedback>> updateTimeAnswerFeedback(@RequestBody Feedback feedback) {
        feedbackService.updateTimeAnswerFeedback(feedback);
        return new ResponseEntity<>(new ResponseDto<>(
                true, feedbackService.updateTimeAnswerFeedback(feedback)), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает обращение в работу
     * @param id - идентификатор обращения
     * @return ResponseEntity
     */
    @PutMapping("/inWork")
    @ApiOperation(value = "Returns the feedback back at work",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<Feedback>> returnInWork(@RequestBody Long id) {
        feedbackService.returnInWork(id);
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackService.returnInWork(id)), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает обращения текущего пользователя
     * @return ResponseEntity<Set<Feedback>>
     */
    @GetMapping("/messages")
    @ApiOperation(value = "gets all feedbacks from the current customer",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<Set<Feedback>>> getAllFeedbackCurrentCustomer() {
        Set<Feedback> feedbackSet = feedbackService.getAllFeedbackCurrentCustomer();
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackSet), HttpStatus.OK);
        //return ResponseEntity.ok(feedbackSet);
    }

    /**
     * Метод возвращает все обращения
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/allMessages")
    @ApiOperation(value = "gets all feedbacks",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<Feedback>>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
        //return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод возвращает все обращения в статуте IN_PROGRESS
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/inProgress")
    @ApiOperation(value = "gets all feedbacks that are in \"IN_PROGRESS\" status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<Feedback>>> getInProgressFeedback() {
        List<Feedback> feedbackList = feedbackService.getInProgressFeedback();
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
        //return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод возвращает все обращения в статуте LATER
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/later")
    @ApiOperation(value = "gets all feedbacks that are in \"LATER\" status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<Feedback>>> getLaterFeedback() {
        List<Feedback> feedbackList = feedbackService.getLaterFeedback();
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
        //return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод возвращает все обращения в статуте RESOLVED
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/resolved")
    @ApiOperation(value = "gets all feedbacks that are in \"RESOLVED\" status",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<Feedback>>> getResolvedFeedback() {
        List<Feedback> feedbackList = feedbackService.getResolvedFeedback();
        return new ResponseEntity<>(new ResponseDto<>(true, feedbackList), HttpStatus.OK);
        //return ResponseEntity.ok(feedbackList);
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
        //return ResponseEntity.ok(localDateTime);
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
        return new ResponseEntity<>(new ResponseDto<>(true, "Feedback successful deleted"), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }
}
