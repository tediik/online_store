package com.jm.online_store.controller.rest;

import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.dto.CommentDto;
import com.jm.online_store.model.dto.ProductForReviewDto;
import com.jm.online_store.model.dto.ReviewDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
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
@RequestMapping("/api/reviews")
@AllArgsConstructor
@Api(description = "Rest controller for reviews")
public class ReviewRestController {

    private final ProductRepository productRepository;
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final BadWordsService badWordsService;

    /**
     * Fetches an arrayList of all product Review by productId and returns JSON representation response
     *
     * @param productId
     * @return ResponseEntity<List<ReviewDto>>
     */
    @GetMapping("/{productId}")
    @ApiOperation(value = "Fetches an arrayList of all product Review by productId " +
            "and returns JSON representation response by product ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<ReviewDto>> findAll(@PathVariable Long productId) {
        List<ReviewDto> reviewDtos = reviewService.findAll(productId).stream()
                .map(ReviewDto::reviewEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDtos);
    }

    /**
     * Fetches an arrayList of all review comments by reviewId and returns JSON representation response
     *
     * @param reviewId
     * @return ResponseEntity<List<CommentDto>>
     */
    @GetMapping("/comments/{reviewId}")
    @ApiOperation(value = "Fetches an arrayList of all review comments by reviewId" +
            " and returns JSON representation response by review ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<CommentDto>> findAllComments(@PathVariable Long reviewId) {
        List<CommentDto> commentDtos = commentService.findAllByReviewId(reviewId).stream()
                .map(CommentDto::commentEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDtos);
    }

    /**
     * Receives productReview and passes it to Service layer for processing
     * previously, searches for forbidden words
     * @param review
     * @return ResponseEntity<productReview> or ResponseEntity<List<String>>
     */
    @PostMapping
    @ApiOperation(value = "Receives productReview requestBody and passes it to Service layer for processing",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 400, message = "Request has an incorrect data")
    public ResponseEntity<?> addReview(@RequestBody @Valid Review review, BindingResult bindingResult) {
        Product productFromDb = productRepository.findById(review.getProductId()).get();
        if (!bindingResult.hasErrors()) {
            String checkText = review.getContent();
            if (!badWordsService.checkEnabledCheckText()) {
                Review savedReview = reviewService.addReview(review);
                productFromDb.setReviews(List.of(savedReview));
                return ResponseEntity.ok().body(ProductForReviewDto.productToDto(productFromDb));
            } else {
                List<String> resultText = badWordsService.checkComment(checkText);
                if (resultText.isEmpty()) {
                    Review savedReview = reviewService.addReview(review);
                    productFromDb.setReviews(List.of(savedReview));
                    return ResponseEntity.ok().body(ProductForReviewDto.productToDto(productFromDb));
                } else {
                    return ResponseEntity.status(201).body(resultText);
                }
            }
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Запрос содержит неверные данные = [%s]", getErrors(bindingResult)));
    }

    private String getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
