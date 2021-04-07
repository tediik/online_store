package com.jm.online_store.model;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review_notifications")
@ApiModel(description = "Сущность уведомление пользователя об ответах на комментарии")
public class AnswerNotifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long idUserComment;

    @Column(name = "review_date")
    @CreationTimestamp
    private LocalDateTime reviewDate;

    @Column(name = "product_id")
    private Long productId;

    @Column(columnDefinition = "TEXT")
    private String content;

    public AnswerNotifications (@NonNull Long idUserComment,
                                    @NonNull String content,
                                    @NonNull Long productId,
                                    @NotNull LocalDateTime reviewDate) {
        this.idUserComment = idUserComment;
        this.content = content;
        this.productId = productId;
        this.reviewDate = reviewDate;
    }

}
