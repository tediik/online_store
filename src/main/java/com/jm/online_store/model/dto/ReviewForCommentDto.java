package com.jm.online_store.model.dto;

import com.jm.online_store.model.Review;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сущность для получения из {@link Review} -> ReviewForCommentDto
 * для дальнейшей отправки в ResponseEntity в виде Json.
 */
@Data
@ApiModel(description =  "Сущность для получения из  Review -> ReviewForCommentDto\n" +
        "  для дальнейшей отправки в ResponseEntity в виде Json")
public class ReviewForCommentDto {
    private Long id;
    private List<CommentDto> comments;

    public static ReviewForCommentDto reviewToDto(Review review) {
        ReviewForCommentDto reviewForCommentDto = new ReviewForCommentDto();
        reviewForCommentDto.setId(review.getId());
        reviewForCommentDto.setComments(review.getComments()
                .stream()
                .map(CommentDto::commentEntityToDto)
                .collect(Collectors.toList()));
        return reviewForCommentDto;
    }
}
