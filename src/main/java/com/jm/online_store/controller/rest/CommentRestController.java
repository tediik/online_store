package com.jm.online_store.controller.rest;

import com.jm.online_store.model.ProductComment;
import com.jm.online_store.service.interf.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentRestController {

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
    public ResponseEntity<ProductComment> addComment(@RequestBody @Valid ProductComment productComment, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            ProductComment savedComment = commentService.addComment(productComment);
            return ResponseEntity.ok().body(savedComment);
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data = [%s]", getErrors(bindingResult)));
    }

    private String getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}

