package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.CommentDto;
import com.jm.online_store.model.dto.ProductForCommentDto;
import com.jm.online_store.model.dto.ReviewForCommentDto;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final UserService userService;


    /**
     * Fetches an arrayList of all product Comments by productId and returns JSON representation response
     *
     * @param productId
     * @return ResponseEntity<List < CommentDto>>
     */
    @GetMapping("/{productId}")
    @ApiOperation(value = "Fetches all the comments from current product")
    public ResponseEntity<List<CommentDto>> findAll(@PathVariable Long productId) {
        List<CommentDto> commentDtos = commentService.findAllByProductId(productId).stream()
                .map(CommentDto::commentEntityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDtos);
    }

    /**
     * Receives productComment requestBody and passes it to Service layer for processing
     * Returns JSON representation
     *
     * @param comment
     * @return ResponseEntity<ProductComment>
     */
    @PostMapping
    @ApiOperation(value = "Post new comment to the current product", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Request contains incorrect data"),
            @ApiResponse(code = 200, message = "Comment was successfully added")
    })
    public ResponseEntity<ProductForCommentDto> addComment(@RequestBody @Valid Comment comment, BindingResult bindingResult) {
        Product productFromDb = productRepository.findById(comment.getProductId()).get();
        if (!bindingResult.hasErrors()) {
            Comment savedComment = commentService.addComment(comment);
            productFromDb.setComments(List.of(savedComment));
            return ResponseEntity.ok().body(ProductForCommentDto.productToDto(productFromDb));
        } else {
            log.debug("Request contains incorrect data = {}", getErrors(bindingResult));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Request contains incorrect data = [%s]", getErrors(bindingResult)));
        }
    }

    /**
     * Receives reviewComment requestBody and reviewId, passes it to Service layer for processing
     * Returns JSON representation
     *
     * @param comment
     * @param reviewId
     * @return ResponseEntity<ReviewForCommentDto>
     */
    @PostMapping("/{reviewId}")
    @ApiOperation(value = "Post new Review comment to the current product", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Request contains incorrect data"),
            @ApiResponse(code = 200, message = "Review comment has successfully added")
    })
    public ResponseEntity<ReviewForCommentDto> addReviewComment(@RequestBody @Valid Comment comment,
                                                                @PathVariable Long reviewId, BindingResult bindingResult) {
        Review reviewFromDb = reviewService.findById(reviewId).get();
        if (!bindingResult.hasErrors()) {
            comment.setReview(reviewFromDb);
            Comment savedComment = commentService.addComment(comment);
            reviewFromDb.setComments(List.of(savedComment));
            CommentDto.commentEntityToDto(comment);
            return ResponseEntity.ok().body(ReviewForCommentDto.reviewToDto(reviewFromDb));
        } else {
            log.debug("Request contains incorrect data = {}", getErrors(bindingResult));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Request contains incorrect data = [%s]", getErrors(bindingResult)));
        }
    }

    /**
     * Удаляет комментарий по его айди
     * @param commentId
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/{commentId}")
    @ApiOperation(value = "Deletes comment by its Id", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Request contains incorrect data - only comment author can delete it"),
            @ApiResponse(code = 200, message = "comment was successfully deleted")

    })
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ResponseEntity<?>[] answer = new ResponseEntity[1];
        userService.findByEmail(email).ifPresentOrElse(e -> {
                    if (e.getId().equals(commentService.findById(commentId).getCustomer().getId())) {
                        commentService.removeById(commentId);
                        answer[0] = new ResponseEntity<>(HttpStatus.OK);
                    } else
                        answer[0] = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                },
                () -> answer[0] = new ResponseEntity<>(HttpStatus.NOT_MODIFIED)
        );
        return answer[0];
    }

    /**
     * Обновляет комментарий
     * @param comment
     * @return ResponseEntity<?>
     */
    @PutMapping
    @ApiOperation(value = "Updates comment", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 304, message = "Request contains incorrect data - only comment author can change it"),
            @ApiResponse(code = 200, message = "comment was successfully updated")
    })
    public ResponseEntity<?> updateComment(@RequestBody @Valid Comment comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ResponseEntity<?>[] answer = new ResponseEntity[1];
        userService.findByEmail(email).ifPresentOrElse(e -> {
                    if (e.getId().equals(commentService.findById(comment.getId()).getCustomer().getId())) {
                        commentService.update(comment);
                        answer[0] = new ResponseEntity<>(HttpStatus.OK);
                    } else
                        answer[0] = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                },
                () -> answer[0] = new ResponseEntity<>(HttpStatus.NOT_MODIFIED)
        );
        return answer[0];
    }

    private String getErrors(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}