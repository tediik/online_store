package com.jm.online_store.model.dto;

import com.jm.online_store.model.Comment;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Data
@Slf4j
public class CommentDto {

   private Long id;
   private Long parentId;
   private String content;
   private String userPhoto;
   private String firstName;
   private String lastName;
   private String userEmail;
   private String timeStamp;
   private Long productId;
   private Long reviewId;
   private Boolean deletedHaveKids;

   public static CommentDto commentEntityToDto(Comment commentEntity){
       CommentDto commentDto = new CommentDto();
       commentDto.setId(commentEntity.getId());
       commentDto.setParentId(commentEntity.getParentId());
       commentDto.setContent(commentEntity.getContent());
       commentDto.setUserPhoto(commentEntity.getCustomer().getProfilePicture());
       commentDto.setFirstName(commentEntity.getCustomer().getFirstName());
       commentDto.setLastName(commentEntity.getCustomer().getLastName());
       commentDto.setUserEmail(commentEntity.getCustomer().getEmail());
       commentDto.setDeletedHaveKids(commentEntity.isDeletedHaveKids());

       DateTimeFormatter dTF2 = DateTimeFormatter.ofPattern("HH:mm yyyy-MM-dd");
       try {
           commentDto.setTimeStamp(formatToYesterdayOrToday(commentEntity.getCommentDate().format(dTF2)));  //commentEntity.getCommentDate().truncatedTo(ChronoUnit.MINUTES));
       } catch (ParseException e) {
           e.printStackTrace();
           log.debug("Ошибка парсинга даты внутри метода formatToYesterdayOrToday(String date) : {}", e);
       }
       commentDto.setProductId(commentEntity.getProductId());
       if (commentEntity.getReview() != null) {
           commentDto.setReviewId(commentEntity.getReview().getId());
       }
       return commentDto;
   }
    /**
     * The method converts the date
     * to the word "today" or "yesterday"
     * or a date and time
     * @param date String data
     * @return the found date
     * @throws ParseException SimpleDateFormat("HH:mm yyyy-MM-dd").parse(date)
     */
    public static String formatToYesterdayOrToday(String date) throws ParseException {
        Date dateTime = new SimpleDateFormat("HH:mm yyyy-MM-dd").parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("hh:mma");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "сегодня " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "вчера " + timeFormatter.format(dateTime);
        } else {
            return date;
        }
    }
}
