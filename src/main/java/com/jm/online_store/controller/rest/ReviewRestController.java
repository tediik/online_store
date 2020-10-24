package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.dto.ProductForReviewDto;
import com.jm.online_store.model.dto.ReviewDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.ReviewService;
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
public class ReviewRestController {

    private final ProductRepository productRepository;
    private final ReviewService reviewService;

    /**
     * Получает arrayList всех отзывов к продукту по productId и возвращает ответ JSON response
     *
     * @param productId
     * @return ResponseEntity<List < ReviewDto>>
     */
    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewDto>> findAll(@PathVariable Long productId) {
        List<ReviewDto> reviewDtos = reviewService.findAll(productId).stream()
                .map(ReviewDto::reviewEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDtos);
    }

    /**
     * Получает productReview requestBody и передает его в слой Service для обработки
     * Возвращает представление JSON
     *
     * @param review
     * @return ResponseEntity<ProductReview>
     */
    @PostMapping
    public ResponseEntity<ProductForReviewDto> addReview(@RequestBody @Valid Review review, BindingResult bindingResult) {
        Product productFromDb = productRepository.findById(review.getProductId()).get();
        if (!bindingResult.hasErrors()) {
            Review savedReview = reviewService.addReview(review);
            productFromDb.setReviews(List.of(savedReview));
            return ResponseEntity.ok().body(ProductForReviewDto.productToDto(productFromDb));
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