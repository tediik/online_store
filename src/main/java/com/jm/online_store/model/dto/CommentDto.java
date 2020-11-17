package com.jm.online_store.model.dto;

import com.jm.online_store.model.Comment;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class CommentDto {

   private Long id;
   private Long parentId;
   private String content;
   private String userPhoto;
   private String userEmail;
   private LocalDateTime timeStamp;
   private Long productId;
   private Long reviewId;

   public static CommentDto commentEntityToDto(Comment commentEntity){
       CommentDto commentDto = new CommentDto();
       commentDto.setId(commentEntity.getId());
       commentDto.setParentId(commentEntity.getParentId());
       commentDto.setContent(commentEntity.getContent());
       commentDto.setUserPhoto(commentEntity.getCustomer().getProfilePicture());
       commentDto.setUserEmail(commentEntity.getCustomer().getEmail());
       commentDto.setTimeStamp(commentEntity.getCommentDate().truncatedTo(ChronoUnit.MINUTES));
       commentDto.setProductId(commentEntity.getProductId());
       if (commentEntity.getReview() != null) {
           commentDto.setReviewId(commentEntity.getReview().getId());
       }
       return commentDto;
   }
}