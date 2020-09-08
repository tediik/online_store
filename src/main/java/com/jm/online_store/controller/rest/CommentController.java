package com.jm.online_store.controller.rest;

import com.jm.online_store.model.ProductComment;
import com.jm.online_store.service.interf.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Fetches an arrayList of all Comments from database and returns JSON representation response
     *
     * @return ResponseEntity<List < ProductComment>>
     */
    @GetMapping
    public ResponseEntity<List<ProductComment>> findAll() {
        return ResponseEntity.ok(commentService.findAll());
    }

    /**
     * Receives productComment requestBody and passes it to Service layer for processing
     * Returns JSON representation
     *
     * @param productComment
     * @return ResponseEntity<ProductComment>
     */
    @PostMapping
    public ResponseEntity<ProductComment> addComment(@RequestBody ProductComment productComment) {
        return ResponseEntity.ok(commentService.addComment(productComment));
    }
}
