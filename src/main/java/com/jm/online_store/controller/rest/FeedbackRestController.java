package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicService;
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
    private final TopicService topicService;
    private final FeedbackService feedbackService;

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getFeedbackCategories() {
        return ResponseEntity.ok(topicService.getAllCategories());
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<Topic>> getTopicsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(topicService.getTopicsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<String> addNewFeedback(@RequestBody Feedback newFeedback) {
        feedbackService.addFeedbackFromDto(newFeedback);
        return ResponseEntity.ok().build();
    }
}
