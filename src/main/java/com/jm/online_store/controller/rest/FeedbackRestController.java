package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicsCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
