package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicsCategoryService;
import lombok.AllArgsConstructor;
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

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
public class FeedbackRestController {
    private final FeedbackService feedbackService;
    private final TopicsCategoryService topicsCategoryService;

    /**
     * Mapping to get categories from {@link TopicsCategory}
     *
     * @return - ResponseEntity with list of actual categories for feedback
     */
    @GetMapping("/categories")
    public ResponseEntity<List<TopicsCategory>> getFeedbackTopicsCategories() {
        return ResponseEntity.ok(topicsCategoryService.findAllByActualIsTrue());
    }

    /**
     * Mapping to get topics list for special category
     *
     * @param category - {@link String} String with name of category
     * @return - ResponseEntity with list of topics for feedback
     */
    @GetMapping("/{category}")
    public ResponseEntity<List<Topic>> getTopicsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(topicsCategoryService.findByCategoryName(category).getTopics());
    }

    /**
     * Mapping to post new feedback
     *
     * @param newFeedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PostMapping
    public ResponseEntity<String> addNewFeedback(@RequestBody Feedback newFeedback) {
        feedbackService.addFeedbackFromDto(newFeedback);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод добавления ответа на обращения
     * @param feedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PutMapping("/addAnswer")
    public ResponseEntity<String> addAnswerFeedback(@RequestBody Feedback feedback) {
        feedbackService.addAnswerFeedback(feedback);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод устанавливает дату и время обращения, для поля responseExpected
     * @param feedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PutMapping("/laterAnswer")
    public ResponseEntity<String> updateTimeAnswerFeedback(@RequestBody Feedback feedback) {
        feedbackService.updateTimeAnswerFeedback(feedback);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает обращение в работу
     * @param id - идентификатор обращения
     * @return ResponseEntity
     */
    @PutMapping("/inWork")
    public ResponseEntity<String> returnInWork(@RequestBody Long id) {
        feedbackService.returnInWork(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает обращения текущего пользователя
     * @return ResponseEntity<Set<Feedback>>
     */
    @GetMapping("/messages")
    public ResponseEntity<Set<Feedback>> getAllFeedbackCurrentCustomer() {
        Set<Feedback> feedbackSet = feedbackService.getAllFeedbackCurrentCustomer();
        return ResponseEntity.ok(feedbackSet);
    }

    /**
     * Метод возвращает все обращения
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/allMessages")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод возвращает все обращения в статуте IN_PROGRESS
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/inProgress")
    public ResponseEntity<List<Feedback>> getInProgressFeedback() {
        List<Feedback> feedbackList = feedbackService.getInProgressFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод возвращает все обращения в статуте LATER
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/later")
    public ResponseEntity<List<Feedback>> getLaterFeedback() {
        List<Feedback> feedbackList = feedbackService.getLaterFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод возвращает все обращения в статуте RESOLVED
     * @return ResponseEntity<List<Feedback>>
     */
    @GetMapping("/resolved")
    public ResponseEntity<List<Feedback>> getResolvedFeedback() {
        List<Feedback> feedbackList = feedbackService.getResolvedFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    /**
     * Метод по идентификатору находит обращение, смотрит значение поля responseExpected,
     * если оно null, возвращаем текущее время, обрезанное до минут, иначе возвращаем
     * значение поля.
     * @param id идентификатор обращения
     * @return ResponseEntity<LocalDateTime>
     */
    @GetMapping("/timeAnswer/{id}")
    public ResponseEntity<LocalDateTime> getResponseExpectedFeedback(@PathVariable Long id) {
        LocalDateTime localDateTime = feedbackService.getDateTimeFeedback(id);
        return ResponseEntity.ok(localDateTime);
    }

    /**
     * Метод удаляет обращение
     * @param id идентификатор обращения
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedbackById(id);
        return ResponseEntity.ok().build();
    }
}
