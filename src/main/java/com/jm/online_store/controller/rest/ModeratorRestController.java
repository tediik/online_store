package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Comment;
import com.jm.online_store.service.interf.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/moderator")
@AllArgsConstructor
public class ModeratorRestController {
    private final CommentService commentService;

    @GetMapping
    public List<Comment> allComments() {
        List<Comment> allComments = commentService.findAll();
        return allComments;
    }
}
