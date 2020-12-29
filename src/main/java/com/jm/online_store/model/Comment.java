package com.jm.online_store.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "Сущность - Comment, связана с сущностью User, Product, Review и ReportComment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @ManyToOne
    @JoinColumn(name = "parent_comment")
    private Comment parentComment;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "comment_date")
    @CreationTimestamp
    private LocalDateTime commentDate;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<ReportComment> reportComments;

    @Column(nullable = false)
    private boolean deletedHasKids = false;

    public Comment(String content) {
        this.content = content;
    }

    public Comment(Long id, String content) {
        this.id = id;
        this.content = content;
    }
    public   String viewDataComment;

    public void setViewDataComment(String viewDataComment) {
        this.viewDataComment = viewDataComment;
    }

    public String getViewDataComment() {
        return viewDataComment;
    }
}
