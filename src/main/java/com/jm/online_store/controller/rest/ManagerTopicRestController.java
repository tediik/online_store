package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Topic;
import com.jm.online_store.service.interf.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager/topic")
@RequiredArgsConstructor
public class ManagerTopicRestController {
    private final TopicService topicService;

    @GetMapping("/{id}") // ok
    public ResponseEntity<Topic> readTopic(@PathVariable(name = "id") long id) {
        if (!topicService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topicService.findById(id));
    }

    @PostMapping // ok
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic){
        if (topicService.existsByTopicName(topic.getTopicName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        topicService.creat(topic);
        return ResponseEntity.ok(topicService.findByTopicName(topic.getTopicName()));
    }

    @PutMapping("/{id}") // ok
    public ResponseEntity<Topic> editTopic(@PathVariable(name = "id") long id, @RequestBody Topic topic) {
        if(!topicService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        topicService.update(topic);
        return ResponseEntity.ok(topicService.findByTopicName(topic.getTopicName()));
    }
}
