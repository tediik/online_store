package com.jm.online_store.model;

import com.jm.online_store.enums.ReportReason;
import io.swagger.annotations.ApiModel;
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
import javax.persistence.Table;

/**
 * Сущность жалоб на {@link Comment}.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "Сущность ReportComment  - жалобы на Comment, связана с Comment")
public class ReportComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Комментарий к причине жалобы.
     */
    @Column(name = "reason_comment")
    private String reasonComment;

    /**
     * Причина жалобы.
     */
    @Column(name = "report_reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    /**
     * Пожаловавшийся пользователь
     */
    @Column(name = "report_customer_email")
    private String reportCustomerEmail;

    /**
     * Комментарий на который пожаловались.
     */
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

}
