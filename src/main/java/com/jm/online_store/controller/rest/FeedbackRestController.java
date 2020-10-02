package com.jm.online_store.controller.rest;

import com.jm.online_store.service.interf.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
public class FeedbackRestController {
    private final TopicService topicService;

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getFeedbackCategories() {
        return ResponseEntity.ok(topicService.getAllCategories());
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<String>> getTopicsByCategory(@PathVariable String category){
        return ResponseEntity.ok(topicService.getTopicsByCategory(category));
    }
}
