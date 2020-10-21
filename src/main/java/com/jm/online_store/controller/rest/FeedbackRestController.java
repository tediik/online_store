package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
public class FeedbackRestController {
    private final TopicService topicService;
    private final FeedbackService feedbackService;
    private final UserService userService;

    /**
     * Mapping to get categories from {@link Topic}
     * @return - ResponseEntity with list of categories for feedback
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getFeedbackCategories() {
        return ResponseEntity.ok(topicService.getAllCategories());
    }

    /**
     * Mapping to get topics list for special category
     * @param category - {@link String} String with name of category
     * @return - ResponseEntity with list of topics for feedback
     */
    @GetMapping("/{category}")
    public ResponseEntity<List<Topic>> getTopicsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(topicService.getTopicsByCategory(category));
    }

    /**
     * Mapping to post new feedback
     * @param newFeedback - {@link Feedback}
     * @return ResponseEntity
     */
    @PostMapping
    public ResponseEntity<String> addNewFeedback(@RequestBody Feedback newFeedback) {
        feedbackService.addFeedbackFromDto(newFeedback);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<Set<Feedback>> getAllFeedbackCurrentCustomer() {
        Set<Feedback> feedbackSet = userService.getCurrentLoggedInUser().getFeedbacks();
        return ResponseEntity.ok(feedbackSet);
    }

    @GetMapping("/allMessages")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/inProgress")
    public ResponseEntity<List<Feedback>> getInProgressFeedback() {
        List<Feedback> feedbackList = feedbackService.getInProgressFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/later")
    public ResponseEntity<List<Feedback>> getLaterFeedback() {
        List<Feedback> feedbackList = feedbackService.getLaterFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/resolved")
    public ResponseEntity<List<Feedback>> getResolvedFeedback() {
        List<Feedback> feedbackList = feedbackService.getResolvedFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedbackById(id);
        return ResponseEntity.ok().build();
    }
}
