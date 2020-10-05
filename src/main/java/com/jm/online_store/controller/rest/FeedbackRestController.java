package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.builder.UserDtoBuilder;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.TopicService;
import com.jm.online_store.service.interf.UserService;
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
    private final UserService userService;
    private final UserDtoBuilder userDtoBuilder;

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

    /**
     * Mapping to get user logged in details such as email and first name
     * @return - ResponseEntity with {@link UserDto} if user are logged in or notFound
     */
    @GetMapping("/userInfo")
    public ResponseEntity<UserDto> getCustomerInfo() {
        User user = userService.getCurrentLoggedInUser();
        if (user != null) {
            UserDto userDto = userDtoBuilder.build(user);
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.notFound().build();
    }
}
