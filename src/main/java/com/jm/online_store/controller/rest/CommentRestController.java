package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductComment;
import com.jm.online_store.model.dto.ProductCommentDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.model.mapper.ProductCommentMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final ProductRepository productRepository;
    private final ProductCommentMapper productCommentMapper;

    /**
     * Fetches an arrayList of all Comments from database and returns JSON representation response
     *
     * @return ResponseEntity<List < ProductComment>>
     */
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductComment>> findAll(@PathVariable Long productId) {


        return ResponseEntity.ok(commentService.findAll(productId));
    }

    /**
     * Receives productComment requestBody and passes it to Service layer for processing
     * Returns JSON representation
     *
     * @param productComment
     * @return ResponseEntity<ProductComment>
     */
    @PostMapping
    public ResponseEntity<ProductCommentDto> addComment(@RequestBody @Valid ProductComment productComment, BindingResult bindingResult) {
        Product productFromDb = productRepository.findById(productComment.getProductId()).get();
        if (!bindingResult.hasErrors()) {
            ProductComment savedComment = commentService.addComment(productComment);
            productFromDb.setComments(List.of(savedComment));
            return ResponseEntity.ok().body(productCommentMapper.toDto(productFromDb));
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request contains incorrect data = [%s]", getErrors(bindingResult)));
    }

    private String getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}

