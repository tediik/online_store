package com.jm.online_store.model.dto;

import com.jm.online_store.model.Review;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class ReviewDto {

    private Long id;
    private String content;
    private String userPhoto;
    private String userEmail;
    private LocalDateTime timeStamp;
    private Long productId;

    public static ReviewDto reviewEntityToDto(Review reviewEntity) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(reviewEntity.getId());
        reviewDto.setContent(reviewEntity.getContent());
        reviewDto.setUserPhoto(reviewEntity.getCustomer().getProfilePicture());
        reviewDto.setUserEmail(reviewEntity.getCustomer().getEmail());
        reviewDto.setTimeStamp(reviewEntity.getReviewDate().truncatedTo(ChronoUnit.MINUTES));
        reviewDto.setProductId(reviewEntity.getProductId());
        return reviewDto;
    }
}