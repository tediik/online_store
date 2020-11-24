package com.jm.online_store.model;

import com.jm.online_store.enums.ReportReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Сущность жалоб на {@link Comment}.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Комментарий к причине жалобы.
     */
    @Column(name = "reasonComment")
    private String reasonComment;

    /**
     * Причина жалобы.
     */
    @Column(name = "reportReason")
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    /**
     * Комментарий на который пожаловались.
     */
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
