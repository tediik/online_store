package com.jm.online_store.controller.rest;

import com.jm.online_store.model.ProductComment;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ProductCommentRepository;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CommentController {

    private final ProductCommentRepository commentRepository;
    private final UserService userService;

    @GetMapping("/comments")
    public ResponseEntity<List<ProductComment>> findAll() {
        return ResponseEntity.ok(commentRepository.findAll());
    }

    @PostMapping("/comments")
    public ResponseEntity<ProductComment> addComment(@RequestBody ProductComment productComment) {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (productComment.getParentId() != null) {
            productComment.setParentComment(commentRepository.findById(productComment.getParentId()).get());
        }
        productComment.setCustomer(userService.findById(userDetails.getId()).get());
        return ResponseEntity.ok(commentRepository.save(productComment));
    }
}
