package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.dto.CommentDto;
import com.jm.online_store.model.dto.ProductForCommentDto;
import com.jm.online_store.model.dto.ReviewForCommentDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CommentRestController {

    private final CommentService commentService;
    private final ReviewService reviewService;
    private final ProductRepository productRepository;
    private final CommonSettingsService commonSettingsService;
    private final BadWordsService badWordsService;

    /**
     * Fetches an arrayList of all product Comments by productId and returns JSON representation response
     *
     * @param productId
     * @return ResponseEntity<List < CommentDto>>
     */
    @GetMapping("/{productId}")
    public ResponseEntity<List<CommentDto>> findAll(@PathVariable Long productId) {
        List<CommentDto> commentDtos = commentService.findAllByProductId(productId).stream()
                .map(CommentDto::commentEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDtos);
    }

    /**
     * Receives productComment requestBody and passes it to Service layer for processing
     * Returns JSON representation, previously, searches for forbidden words
     *
     * @param comment
     * @return ResponseEntity<ProductComment> or ResponseEntity<List<String>>
     */
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody @Valid Comment comment, BindingResult bindingResult) {
        Product productFromDb = productRepository.findById(comment.getProductId()).get();
        if (!bindingResult.hasErrors()) {
            String checkText = comment.getContent();
            if (!checkEnabledCheckText()) {
                Comment savedComment = commentService.addComment(comment);
                productFromDb.setComments(List.of(savedComment));
                return ResponseEntity.ok().body(ProductForCommentDto.productToDto(productFromDb));
            } else {
                List<String> resultText = checkText(checkText);
                if (resultText.isEmpty()) {
                    Comment savedComment = commentService.addComment(comment);
                    productFromDb.setComments(List.of(savedComment));
                    return ResponseEntity.ok().body(ProductForCommentDto.productToDto(productFromDb));
                } else {
                    return ResponseEntity.status(201).body(resultText);
                }
            }
        } else {
            log.debug("Request contains incorrect data = {}", getErrors(bindingResult));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Request contains incorrect data = [%s]", getErrors(bindingResult)));
        }
    }

    /**
     * Receives reviewComment requestBody and reviewId, passes it to Service layer for processing
     * previously, searches for forbidden words
     * Returns JSON representation
     *
     * @param comment
     * @param reviewId
     * @return ResponseEntity<ReviewForCommentDto> or ResponseEntity<List<String>>
     */
    @PostMapping("/{reviewId}")
    public ResponseEntity<?> addReviewComment(@RequestBody @Valid Comment comment,
                                              @PathVariable Long reviewId, BindingResult bindingResult) {
        Review reviewFromDb = reviewService.findById(reviewId).get();
        if (!bindingResult.hasErrors()) {
            String checkText = comment.getContent();
            if (!checkEnabledCheckText()) {
                comment.setReview(reviewFromDb);
                Comment savedComment = commentService.addComment(comment);
                reviewFromDb.setComments(List.of(savedComment));
                CommentDto.commentEntityToDto(comment);
                return ResponseEntity.ok().body(ReviewForCommentDto.reviewToDto(reviewFromDb));
            } else {
                List<String> resultText = checkText(checkText);
                if (resultText.isEmpty()) {
                    comment.setReview(reviewFromDb);
                    Comment savedComment = commentService.addComment(comment);
                    reviewFromDb.setComments(List.of(savedComment));
                    CommentDto.commentEntityToDto(comment);
                    return ResponseEntity.ok().body(ReviewForCommentDto.reviewToDto(reviewFromDb));
                } else {
                    return ResponseEntity.status(201).body(resultText);
                }

            }
        } else {
            log.debug("Request contains incorrect data = {}", getErrors(bindingResult));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Request contains incorrect data = [%s]", getErrors(bindingResult)));
        }
    }

    private String getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    private boolean checkEnabledCheckText() {
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        if (templateBody.getTextValue().equals("false")) return false;
        return true;
    }

    private List<String> checkText(String checkText) {
        return badWordsService.checkComment(checkText);
    }
}